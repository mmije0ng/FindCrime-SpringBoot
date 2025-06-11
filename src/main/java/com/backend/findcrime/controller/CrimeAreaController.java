package com.backend.findcrime.controller;

import com.backend.findcrime.apiPayload.ApiResponse;
import com.backend.findcrime.service.CrimeAreaService.CrimeAreaCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "지역 범죄", description = "지역 범죄에 관한 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/crime-areas")
public class CrimeAreaController {

    private final CrimeAreaCommandService crimeAreaCommandService;

    @PostMapping("/map-all")
    @Operation(summary = "모든 범죄와 지역 데이터를 매핑")
    public ApiResponse<String> mapAllCrimesToAreas() {
        crimeAreaCommandService.mapAllCrimesToAllAreas();
        return ApiResponse.onSuccess("모든 범죄와 지역이 매핑되었습니다.");
    }
}
