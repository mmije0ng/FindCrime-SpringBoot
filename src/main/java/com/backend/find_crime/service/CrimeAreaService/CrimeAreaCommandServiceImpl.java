package com.backend.find_crime.service.CrimeAreaService;

import com.backend.find_crime.domain.Area;
import com.backend.find_crime.domain.Crime;
import com.backend.find_crime.domain.mapping.CrimeArea;
import com.backend.find_crime.repository.AreaRepository.AreaRepository;
import com.backend.find_crime.repository.CrimeAreaRepository.CrimeAreaRepository;
import com.backend.find_crime.repository.CrimeRepository.CrimeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 리팩토링 사항
 * 1) 기존: crime×area 반복마다 exists 쿼리 → N×M 호출 가능 (심각한 I/O 병목)
 * 2) 변경: 기존 매핑을 한 번에 Set으로 로딩 → 메모리에서 O(1) 조회
 * 3) 대량 insert는 청크로 끊어 saveAll + flush/clear (메모리/DB 부하 완화)
 * 4) (권장) crime_area 테이블에 (crime_id, area_id) UNIQUE 제약 필수
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class CrimeAreaCommandServiceImpl implements CrimeAreaCommandService {

    private final CrimeAreaRepository crimeAreaRepository;
    private final CrimeRepository crimeRepository;
    private final AreaRepository areaRepository;

    @PersistenceContext
    private EntityManager em;

    // 한 번에 insert할 청크 크기 (환경에 맞게 조정)
    private static final int BATCH_SIZE = 1000;

    /**
     * 모든 범죄를 모든 지역에 매핑 (없는 조합만 생성)
     * - 대량 데이터 안전: Set 기반 중복체크 + 배치 저장
     */
    @Transactional
    public void mapAllCrimesToAllAreas() {
        // 1) 기준 데이터 일괄 로딩
        List<Crime> crimes = crimeRepository.findAll();
        List<Area> areas = areaRepository.findAll();
        log.info("기준 로딩 완료 - crimes={}, areas={}", crimes.size(), areas.size());

        if (crimes.isEmpty() || areas.isEmpty()) {
            log.info("생성할 매핑 없음(기준 데이터 부족)");
            return;
        }

        // 2) 기존 매핑을 (crimeId#areaId) 형태 Set으로 일괄 적재 → 메모리 O(1) 조회
        //    예: "12#345" 같은 키
        Set<String> existingKeys = new HashSet<>(Math.max(16, crimes.size() * areas.size() / 10)); // 대략적인 capacity
        crimeAreaRepository.findAll()
                .forEach(ca -> existingKeys.add(key(ca.getCrime().getId(), ca.getArea().getId())));
        log.info("기존 매핑 로딩 완료 - existingMappings={}", existingKeys.size());

        // 3) 신규 매핑 생성(없는 조합만)
        List<CrimeArea> buffer = new ArrayList<>(BATCH_SIZE);
        long newCount = 0L;

        // 외부 2중 루프는 어쩔 수 없지만, DB hit 없이 Set으로 필터링하므로 빠름
        for (Crime crime : crimes) {
            final Long cId = crime.getId();
            for (Area area : areas) {
                final Long aId = area.getId();
                if (!existingKeys.contains(key(cId, aId))) {
                    buffer.add(CrimeArea.builder()
                            .crime(crime)
                            .area(area)
                            .build());
                    // 배치 단위로 저장
                    if (buffer.size() >= BATCH_SIZE) {
                        persistBatch(buffer);
                        newCount += buffer.size();
                        buffer.clear();
                    }
                }
            }
        }

        // 잔여 버퍼 저장
        if (!buffer.isEmpty()) {
            persistBatch(buffer);
            newCount += buffer.size();
            buffer.clear();
        }

        log.info("범죄-지역 매핑 저장 완료 - 신규 생성 수={} (기존 {} 포함 총 예상 조합수 ~ {}개)",
                newCount, existingKeys.size(), (long) crimes.size() * (long) areas.size());
    }

    /** (crimeId#areaId) 키 생성 */
    private static String key(Long crimeId, Long areaId) {
        return crimeId + "#" + areaId;
    }

    /** 배치 저장 + flush/clear로 영속성 컨텍스트 메모리 해제 */
    @Transactional
    protected void persistBatch(List<CrimeArea> batch) {
        crimeAreaRepository.saveAll(batch);
        // JPA 2차 캐시/영속성 컨텍스트 비우기 (메모리 압박/스냅샷 비대 방지)
        em.flush();
        em.clear();
    }
}
