package com.backend.find_crime.service.CrimeAreaStatisticService;

import com.backend.find_crime.domain.Area;
import com.backend.find_crime.domain.Crime;
import com.backend.find_crime.domain.CrimeAreaStatistic;
import com.backend.find_crime.domain.mapping.CrimeArea;
import com.backend.find_crime.repository.AreaRepository.AreaRepository;
import com.backend.find_crime.repository.CrimeAreaRepository.CrimeAreaRepository;
import com.backend.find_crime.repository.CrimeAreaStatisticRepository.CrimeAreaStatisticRepository;
import com.backend.find_crime.repository.CrimeRepository.CrimeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class CrimeAreaStatisticCommandServiceImpl implements CrimeAreaStatisticCommandService {

    private final CrimeAreaRepository crimeAreaRepository;
    private final CrimeRepository crimeRepository;
    private final AreaRepository areaRepository;
    private final CrimeAreaStatisticRepository statisticRepository;

    @PersistenceContext
    private EntityManager em;

    /** 대량 저장 시 배치 크기 */
    private static final int BATCH_SIZE = 1000;

    /**
     * CSV를 처리해 주어진 연도의 통계를 적재/갱신.
\     * - 모든 참조/기존통계를 1회 로드해서 메모리 맵으로 사용
     * - 배치 upsert(saveAll + flush/clear)로 데이터 누락/부하 완화
     */
    @Transactional
    public void processCsvDataForYear(int year) {
        try (
                InputStream is = new FileInputStream("data/경찰청_범죄 발생 지역별 통계_20231231.csv");
                InputStreamReader reader = new InputStreamReader(is, Charset.forName("MS949"));
                CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader)
        ) {
            // ===== 0) 기준 데이터 한 번에 로드/정규화 캐시 =====
            List<Area> allAreas = areaRepository.findAll();
            Map<String, Area> areaByKey = allAreas.stream()
                    .collect(Collectors.toMap(
                            a -> normalize(a.getAreaName() + a.getAreaDetailName()),
                            Function.identity()
                    ));
            // (areaId 기준 맵도 추가)
            Map<Long, Area> areaById = allAreas.stream()
                    .collect(Collectors.toMap(Area::getId, Function.identity()));

            Map<String, Crime> crimeByKey = crimeRepository.findAll().stream()
                    .collect(Collectors.toMap(
                            c -> normalize(c.getCrimeType() + ":" + c.getCrimeDetailType()),
                            Function.identity()
                    ));
            Map<Long, Crime> crimeById = crimeByKey.values().stream()
                    .collect(Collectors.toMap(Crime::getId, Function.identity()));

            Map<String, CrimeArea> crimeAreaByKey = crimeAreaRepository.findAll().stream()
                    .collect(Collectors.toMap(
                            ca -> ca.getCrime().getId() + ":" + ca.getArea().getId(),
                            Function.identity()
                    ));

            // 해당 연도의 기존 통계를 전부 메모리로 미리 로드하여 upsert에 사용
            List<CrimeAreaStatistic> existingStats = statisticRepository.findAllByCrimeYear(year);
            // 키: crimeAreaId
            Map<Long, CrimeAreaStatistic> statByCrimeAreaId = existingStats.stream()
                    .collect(Collectors.toMap(stat -> stat.getCrimeArea().getId(), Function.identity()));

            // ===== 1) 세부 지역 통계 upsert (배치 버퍼) =====
            List<CrimeAreaStatistic> buffer = new ArrayList<>(BATCH_SIZE);
            int processedRows = 0;

            for (CSVRecord record : parser) {
                String crimeType = record.get("범죄대분류");
                String crimeDetailType = record.get("범죄중분류");
                String crimeKey = normalize(crimeType + ":" + crimeDetailType);

                Crime crime = crimeByKey.get(crimeKey);
                if (crime == null) {
                    // 정의되지 않은 범죄코드 → 스킵
                    continue;
                }

                // CSV 헤더(지역 열) 순회
                for (Map.Entry<String, String> e : record.toMap().entrySet()) {
                    String regionRaw = e.getKey().trim();
                    if (regionRaw.equals("범죄대분류") || regionRaw.equals("범죄중분류")) continue;

                    Area area = areaByKey.get(normalize(regionRaw));
                    if (area == null) {
                        // 정의되지 않은 지역명 → 스킵
                        continue;
                    }

                    int count = parseInt(e.getValue());
                    String caKey = crime.getId() + ":" + area.getId();
                    CrimeArea crimeArea = crimeAreaByKey.get(caKey);
                    if (crimeArea == null) {
                        // CrimeArea 매핑이 없으면 스킵
                        continue;
                    }

                    // 기존 통계 upsert (메모리 맵 사용)
                    CrimeAreaStatistic stat = statByCrimeAreaId.get(crimeArea.getId());
                    if (stat == null) {
                        stat = CrimeAreaStatistic.builder()
                                .crimeArea(crimeArea)
                                .crimeYear(year)
                                .crimeCount(count)
                                .build();
                        statByCrimeAreaId.put(crimeArea.getId(), stat);
                    } else {
                        stat.setCrimeCount(count); // 갱신
                    }
                    buffer.add(stat);

                    // 배치 저장
                    if (buffer.size() >= BATCH_SIZE) {
                        statisticRepository.saveAll(buffer);
                        em.flush();
                        em.clear(); // 1차 캐시 정리로 메모리/dirty checking 부하 완화
                        buffer.clear();
                    }
                }

                processedRows++;
                if ((processedRows % 500) == 0) {
                    log.info("CSV 진행상황: {} rows 처리", processedRows);
                }
            }

            // 남은 버퍼 플러시
            if (!buffer.isEmpty()) {
                statisticRepository.saveAll(buffer);
                em.flush();
                em.clear();
                buffer.clear();
            }

            // ===== 2) (시/도) 전체(…전체) 통계 메모리 합산 후 upsert =====
            // ex) "서울전체" 처럼 detailName이 전체인 지역, 단 "전국전체" 제외
            List<Area> totalAreas = allAreas.stream()
                    .filter(a -> a.getAreaDetailName().endsWith("전체") && !a.getAreaName().equals("전국"))
                    .toList();

            // crimeAreaByKey 재사용, statByCrimeAreaId 재사용
            // 총계 계산을 위해, 같은 시/도의 하위 지역 id 리스트를 미리 만든다.
            Map<String, List<Long>> childrenAreaIdsByBase = new HashMap<>();
            // key: "서울" → [서울-강남, 서울-송파, …] (…전체 제외)
            allAreas.forEach(a -> {
                if (!a.getAreaDetailName().endsWith("전체") && !"전국".equals(a.getAreaName())) {
                    childrenAreaIdsByBase
                            .computeIfAbsent(a.getAreaName(), k -> new ArrayList<>())
                            .add(a.getId());
                }
            });

            List<CrimeAreaStatistic> totalUpserts = new ArrayList<>(BATCH_SIZE);

            for (Area totalArea : totalAreas) {
                String baseRegion = totalArea.getAreaName();
                List<Long> childAreaIds = childrenAreaIdsByBase.getOrDefault(baseRegion, List.of());

                for (Crime crime : crimeById.values()) {
                    // 자식 지역들의 해당 범죄 카운트를 전부 합산
                    int totalCount = childAreaIds.stream()
                            .map(childId -> {
                                String key = crime.getId() + ":" + childId;
                                CrimeArea ca = crimeAreaByKey.get(key);
                                if (ca == null) return 0;
                                CrimeAreaStatistic s = statByCrimeAreaId.get(ca.getId());
                                return (s == null ? 0 : s.getCrimeCount());
                            })
                            .mapToInt(Integer::intValue)
                            .sum();

                    // (범죄, totalArea) 로의 crimeArea 찾기
                    String totalKey = crime.getId() + ":" + totalArea.getId();
                    CrimeArea totalCrimeArea = crimeAreaByKey.get(totalKey);
                    if (totalCrimeArea == null) continue;

                    CrimeAreaStatistic existing = statByCrimeAreaId.get(totalCrimeArea.getId());
                    if (existing == null) {
                        existing = CrimeAreaStatistic.builder()
                                .crimeArea(totalCrimeArea)
                                .crimeYear(year)
                                .crimeCount(totalCount)
                                .build();
                        statByCrimeAreaId.put(totalCrimeArea.getId(), existing);
                    } else {
                        existing.setCrimeCount(totalCount);
                    }
                    totalUpserts.add(existing);

                    if (totalUpserts.size() >= BATCH_SIZE) {
                        statisticRepository.saveAll(totalUpserts);
                        em.flush();
                        em.clear();
                        totalUpserts.clear();
                    }
                }
            }

            if (!totalUpserts.isEmpty()) {
                statisticRepository.saveAll(totalUpserts);
                em.flush();
                em.clear();
                totalUpserts.clear();
            }

            // ===== 3) 전국전체 통계 (각 시/도 전체 합) 메모리 합산 후 upsert =====
            Optional<Area> nationalAreaOpt = allAreas.stream()
                    .filter(a -> a.getAreaName().equals("전국") && a.getAreaDetailName().equals("전국전체"))
                    .findFirst();

            if (nationalAreaOpt.isPresent()) {
                Area nationalArea = nationalAreaOpt.get();

                // 시/도 전체만 추출 (…전체 && 전국 제외)
                List<Area> provinceTotals = allAreas.stream()
                        .filter(a -> a.getAreaDetailName().endsWith("전체") && !a.getAreaName().equals("전국"))
                        .toList();

                List<CrimeAreaStatistic> nationalUpserts = new ArrayList<>(BATCH_SIZE);

                for (Crime crime : crimeById.values()) {
                    int nationalSum = provinceTotals.stream()
                            .map(provTotalArea -> {
                                String k = crime.getId() + ":" + provTotalArea.getId();
                                CrimeArea ca = crimeAreaByKey.get(k);
                                if (ca == null) return 0;
                                CrimeAreaStatistic s = statByCrimeAreaId.get(ca.getId());
                                return (s == null ? 0 : s.getCrimeCount());
                            })
                            .mapToInt(Integer::intValue)
                            .sum();

                    String nationalKey = crime.getId() + ":" + nationalArea.getId();
                    CrimeArea nationalCrimeArea = crimeAreaByKey.get(nationalKey);
                    if (nationalCrimeArea == null) continue;

                    CrimeAreaStatistic existing = statByCrimeAreaId.get(nationalCrimeArea.getId());
                    if (existing == null) {
                        existing = CrimeAreaStatistic.builder()
                                .crimeArea(nationalCrimeArea)
                                .crimeYear(year)
                                .crimeCount(nationalSum)
                                .build();
                        statByCrimeAreaId.put(nationalCrimeArea.getId(), existing);
                    } else {
                        existing.setCrimeCount(nationalSum);
                    }
                    nationalUpserts.add(existing);

                    if (nationalUpserts.size() >= BATCH_SIZE) {
                        statisticRepository.saveAll(nationalUpserts);
                        em.flush();
                        em.clear();
                        nationalUpserts.clear();
                    }
                }

                if (!nationalUpserts.isEmpty()) {
                    statisticRepository.saveAll(nationalUpserts);
                    em.flush();
                    em.clear();
                    nationalUpserts.clear();
                }
            }

            log.info("CSV 처리 완료: year={}, 통계 엔트리 총 {}건", year, statByCrimeAreaId.size());

        } catch (Exception e) {
            log.error("CSV 처리 중 오류", e);
            throw new RuntimeException("CSV 처리 중 오류", e);
        }
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private String normalize(String value) {
        return value.replaceAll("\\s+", "").trim();
    }
}
