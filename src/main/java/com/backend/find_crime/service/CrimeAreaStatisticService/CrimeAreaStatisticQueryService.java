package com.backend.find_crime.service.CrimeAreaStatisticService;

import com.backend.find_crime.dto.statistic.CrimeAreaStatisticRequest;
import com.backend.find_crime.dto.statistic.CrimeAreaStatisticResponse;

public interface CrimeAreaStatisticQueryService {

    // 카테고리(연도, 지역, 지역 디테일, 범죄대분류, 범죄소분류)별 통계 조회
    CrimeAreaStatisticResponse.StatisticResultDTO findStatisticByCategories(CrimeAreaStatisticRequest.StatisticRequestDTO requestDTO);

    // 범죄,


}
