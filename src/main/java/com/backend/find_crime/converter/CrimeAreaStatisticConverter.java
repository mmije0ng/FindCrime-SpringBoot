package com.backend.find_crime.converter;

import com.backend.find_crime.domain.CrimeAreaStatistic;
import com.backend.find_crime.dto.statistic.CrimeAreaStatisticResponse;

public class CrimeAreaStatisticConverter {

    public static CrimeAreaStatisticResponse.StatisticResultDTO toStatisticResultDTO(Integer count, String risk){
        return CrimeAreaStatisticResponse.StatisticResultDTO.builder()
                .crimeCount(count)
                .crimeRisk(risk)
                .build();
    }
}
