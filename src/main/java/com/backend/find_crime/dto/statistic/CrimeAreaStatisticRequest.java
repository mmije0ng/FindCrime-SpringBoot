package com.backend.find_crime.dto.statistic;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class CrimeAreaStatisticRequest {

    @Getter @Setter
    @NoArgsConstructor
    @Schema(description = "카테고리별 지역 범죄 통계 요청 정보")
    public static class StatisticRequestDTO {

        @Schema(description = "범죄 연도", example = "2023")
        @NotNull
        private Integer year;

        @Schema(description = "지역 이름", example = "서울")
        @NotNull
        private String areaName;

        @Schema(description = "지역 디테일 이름", example = "성북구")
        @NotNull
        private String areaDetailName;

        @Schema(description = "범죄대분류", example = "지능범죄")
        @NotNull
        private String crimeType;

        @Schema(description = "범죄소분류", example = "사기")
        @NotNull
        private String crimeDetailType;
    }
}
