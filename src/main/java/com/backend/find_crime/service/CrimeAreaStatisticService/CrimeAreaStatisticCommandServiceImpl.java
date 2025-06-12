package com.backend.find_crime.service.CrimeAreaStatisticService;

import com.backend.find_crime.domain.Area;
import com.backend.find_crime.domain.Crime;
import com.backend.find_crime.domain.CrimeAreaStatistic;
import com.backend.find_crime.domain.mapping.CrimeArea;
import com.backend.find_crime.repository.AreaRepository.AreaRepository;
import com.backend.find_crime.repository.CrimeAreaRepository.CrimeAreaRepository;
import com.backend.find_crime.repository.CrimeAreaStatisticRepository.CrimeAreaStatisticRepository;
import com.backend.find_crime.repository.CrimeRepository.CrimeRepository;
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
@Transactional(readOnly = true)
@Service
public class CrimeAreaStatisticCommandServiceImpl implements CrimeAreaStatisticCommandService {

    private final CrimeAreaRepository crimeAreaRepository;
    private final CrimeRepository crimeRepository;
    private final AreaRepository areaRepository;
    private final CrimeAreaStatisticRepository statisticRepository;

    @Transactional
    public void processCsvDataForYear(int year) {
        try (
                InputStream is = new FileInputStream("data/경찰청_범죄 발생 지역별 통계_20231231.csv");
                InputStreamReader reader = new InputStreamReader(is, Charset.forName("MS949"))
        ) {
            CSVParser parser = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(reader);

            List<Area> allAreas = areaRepository.findAll();
            Map<String, Area> areaMap = allAreas.stream()
                    .collect(Collectors.toMap(
                            area -> normalize(area.getAreaName() + area.getAreaDetailName()),
                            Function.identity()
                    ));

            Map<String, Crime> crimeMap = crimeRepository.findAll().stream()
                    .collect(Collectors.toMap(
                            crime -> normalize(crime.getCrimeType() + ":" + crime.getCrimeDetailType()),
                            Function.identity()
                    ));

            Map<String, CrimeArea> crimeAreaMap = crimeAreaRepository.findAll().stream()
                    .collect(Collectors.toMap(
                            ca -> ca.getCrime().getId() + ":" + ca.getArea().getId(),
                            Function.identity()
                    ));

            // Step 1: 세부 지역 통계 저장
            for (CSVRecord record : parser) {
                String crimeType = record.get("범죄대분류");
                String crimeDetailType = record.get("범죄중분류");
                String crimeKey = normalize(crimeType + ":" + crimeDetailType);
                Crime crime = crimeMap.get(crimeKey);
                if (crime == null) return;

                record.toMap().forEach((regionRaw, value) -> {
                    String region = regionRaw.trim();
                    if (region.equals("범죄대분류") || region.equals("범죄중분류")) return;

                    int count = parseInt(value);
                    Area area = areaMap.get(normalize(region));
                    if (area == null) return;

                    CrimeArea crimeArea = crimeAreaMap.get(crime.getId() + ":" + area.getId());
                    if (crimeArea == null) return;

                    statisticRepository.findByCrimeAreaAndCrimeYear(crimeArea, year).ifPresentOrElse(
                            stat -> {
                                stat.setCrimeCount(count);
                                statisticRepository.save(stat);
                            },
                            () -> statisticRepository.save(
                                    CrimeAreaStatistic.builder()
                                            .crimeArea(crimeArea)
                                            .crimeYear(year)
                                            .crimeCount(count)
                                            .build()
                            )
                    );
                });
            }

            // Step 2: 지역 전체(서울전체 등) 통계 계산
            List<Area> totalAreas = allAreas.stream()
                    .filter(a -> a.getAreaDetailName().endsWith("전체") && !a.getAreaName().equals("전국"))
                    .toList();

            for (Area totalArea : totalAreas) {
                String baseRegion = totalArea.getAreaName(); // ex. 서울

                for (Crime crime : crimeMap.values()) {
                    List<CrimeAreaStatistic> partialStats = statisticRepository.findAllByCrimeYear(year).stream()
                            .filter(stat -> {
                                CrimeArea ca = stat.getCrimeArea();
                                Area a = ca.getArea();
                                return ca.getCrime().getId().equals(crime.getId())
                                        && a.getAreaName().equals(baseRegion)
                                        && !a.getAreaDetailName().endsWith("전체");
                            })
                            .toList();

                    int totalCount = partialStats.stream()
                            .mapToInt(CrimeAreaStatistic::getCrimeCount)
                            .sum();

                    CrimeArea totalCrimeArea = crimeAreaMap.get(crime.getId() + ":" + totalArea.getId());
                    if (totalCrimeArea == null) continue;

                    statisticRepository.findByCrimeAreaAndCrimeYear(totalCrimeArea, year).ifPresentOrElse(
                            stat -> {
                                stat.setCrimeCount(totalCount);
                                statisticRepository.save(stat);
                            },
                            () -> statisticRepository.save(
                                    CrimeAreaStatistic.builder()
                                            .crimeArea(totalCrimeArea)
                                            .crimeYear(year)
                                            .crimeCount(totalCount)
                                            .build()
                            )
                    );
                }
            }

            // Step 3: 전국 전체 통계 계산 (서울전체 + 경기도전체 + ... 전체 지역의 합)
            Optional<Area> nationalAreaOpt = allAreas.stream()
                    .filter(a -> a.getAreaName().equals("전국") && a.getAreaDetailName().equals("전국전체"))
                    .findFirst();

            if (nationalAreaOpt.isPresent()) {
                Area nationalArea = nationalAreaOpt.get();

                for (Crime crime : crimeMap.values()) {
                    int totalNationalCount = statisticRepository.findAllByCrimeYear(year).stream()
                            .filter(stat -> {
                                CrimeArea ca = stat.getCrimeArea();
                                Area a = ca.getArea();
                                return ca.getCrime().getId().equals(crime.getId())
                                        && a.getAreaDetailName().endsWith("전체")
                                        && !a.getAreaName().equals("전국"); // 전국전체 제외
                            })
                            .mapToInt(CrimeAreaStatistic::getCrimeCount)
                            .sum();

                    CrimeArea nationalCrimeArea = crimeAreaMap.get(crime.getId() + ":" + nationalArea.getId());
                    if (nationalCrimeArea == null) continue;

                    statisticRepository.findByCrimeAreaAndCrimeYear(nationalCrimeArea, year).ifPresentOrElse(
                            stat -> {
                                stat.setCrimeCount(totalNationalCount);
                                statisticRepository.save(stat);
                            },
                            () -> statisticRepository.save(
                                    CrimeAreaStatistic.builder()
                                            .crimeArea(nationalCrimeArea)
                                            .crimeYear(year)
                                            .crimeCount(totalNationalCount)
                                            .build()
                            )
                    );
                }
            }

        } catch (Exception e) {
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
