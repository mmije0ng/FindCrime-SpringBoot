package com.backend.find_crime.dto.statistic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CrimeAreaStatisticResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "카테고리별 지역 범죄 통계 응답 정보")
    public static class StatisticResultDTO {
        @Schema(description = "범죄 건수", example = "100")
        Integer crimeCount;

        @Schema(description = "범죄 위험 정도", example = "위험")
        String crimeRisk;
    }
}
