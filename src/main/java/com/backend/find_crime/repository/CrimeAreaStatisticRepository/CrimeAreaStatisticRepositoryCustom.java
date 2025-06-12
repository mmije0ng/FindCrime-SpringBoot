package com.backend.find_crime.repository.CrimeAreaStatisticRepository;

import com.backend.find_crime.domain.CrimeAreaStatistic;

import java.util.Optional;

public interface CrimeAreaStatisticRepositoryCustom {
    // 카테고리(연도, 지역, 지역 디테일, 범죄대분류, 범죄소분류)별 통계 조회
    Optional<CrimeAreaStatistic> findStatisticWithJoins(int year, String areaName, String areaDetailName, String crimeType, String crimeDetailType);
}
