package com.backend.findcrime.controller;

import com.backend.findcrime.apiPayload.ApiResponse;
import com.backend.findcrime.service.CrimeAreaStatisticService.CrimeAreaStatisticCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "지역 범죄 통계", description = "지역 범죄 통계 API")
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
@RestController
public class CrimeAreaStatisticController {

    private final CrimeAreaStatisticCommandService statisticCommandService;

    @PostMapping("/upload")
    @Operation(summary = "csv 파일을 통해 범죄 통계 데이터를 DB에 저장")
    public ApiResponse<String> uploadStatistics(@RequestParam("year") int year) {
        statisticCommandService.processCsvDataForYear(year);
        return ApiResponse.onSuccess(year + " 경찰청 범죄 발생 지역별 통계 CSV 데이터 처리 완료");
    }
}
