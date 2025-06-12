package com.backend.find_crime.dto.statistic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CrimeAreaStatisticRequest {

    @Getter @Setter
    @NoArgsConstructor
    @Schema(description = "카테고리별 지역 범죄 통계 요청 정보")
    public static class StatisticRequestDTO {

        @Schema(description = "범죄 연도", example = "2023")
        private Integer year;

        @Schema(description = "지역 이름", example = "서울")
        private String areaName;

        @Schema(description = "지역 디테일 이름", example = "성북구")
        private String areaDetailName;

        @Schema(description = "범죄대분류", example = "지능범죄")
        private String crimeType;

        @Schema(description = "범죄소분류", example = "사기")
        private String crimeDetailType;
    }
}
