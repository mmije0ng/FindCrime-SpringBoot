package com.backend.find_crime.repository.AreaRepository;

import com.backend.find_crime.domain.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AreaRepository extends JpaRepository<Area, Long> {
    // 예: "서울중구" → areaName: 서울, areaDetailName: 중구
    Optional<Area> findByAreaNameAndAreaDetailName(String areaName, String areaDetailName);

    // 전체 구분 명칭이 DB에 저장된 상태라면 이를 한 번에 조회
    default Optional<Area> findByAreaFullName(String fullName) {
        return findAll().stream()
                .filter(area -> (area.getAreaName() + area.getAreaDetailName()).equals(fullName))
                .findFirst();
    }
}
