package com.backend.find_crime.controller;

import com.backend.find_crime.apiPayload.ApiResponse;
import com.backend.find_crime.dto.statistic.CrimeAreaStatisticRequest;
import com.backend.find_crime.dto.statistic.CrimeAreaStatisticResponse;
import com.backend.find_crime.service.CrimeAreaStatisticService.CrimeAreaStatisticCommandService;
import com.backend.find_crime.service.CrimeAreaStatisticService.CrimeAreaStatisticQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "지역 범죄 통계", description = "지역 범죄 통계 API")
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
@RestController
public class CrimeAreaStatisticController {

    private final CrimeAreaStatisticQueryService statisticQueryService;
    private final CrimeAreaStatisticCommandService statisticCommandService;

    @Operation(summary = "카테고리별 지역 범죄 통계 조회 API")
    @GetMapping
    public ApiResponse<CrimeAreaStatisticResponse.StatisticResultDTO> getCrimeAreaStatistic(
            @ModelAttribute CrimeAreaStatisticRequest.StatisticRequestDTO requestDTO) {

        return ApiResponse.onSuccess(statisticQueryService.findStatisticByCategories(requestDTO));
    }

    @Operation(summary = "csv 파일을 통해 범죄 통계 데이터를 DB에 저장")
    @PostMapping("/upload")
    public ApiResponse<String> uploadStatistics(@RequestParam("year") int year) {
        statisticCommandService.processCsvDataForYear(year);
        return ApiResponse.onSuccess(year + " 경찰청 범죄 발생 지역별 통계 CSV 데이터 처리 완료");
    }
}
