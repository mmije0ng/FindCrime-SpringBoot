package com.backend.find_crime.controller;

import com.backend.find_crime.apiPayload.ApiResponse;
import com.backend.find_crime.dto.member.MemberResponse;
import com.backend.find_crime.service.AuthService.AuthService;
import com.backend.find_crime.service.OAuthService.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "로그인/회원가입", description = "인증에 관한 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OAuthService oAuthService;
    private final AuthService authService;

    // 카카오 로그인 API
    @Operation(summary = "카카오 로그인 API",description = "카카오 소셜 로그인 API입니다.")
    @PostMapping("/login/kakao")
    public ApiResponse<String> kakaoOAuthLogin(HttpServletRequest request, HttpServletResponse response) {
        log.info("카카오 로그인 요청");
        oAuthService.kakaoOAuthLoginWithAccessToken(request, response);
        return ApiResponse.onSuccess("카카오 로그인 성공");
    }

    // 토큰 재발급 API
    // Access & RefreshToken 재발급 API
    @Operation(
            summary = "토큰 재발급 API",
            description = "만료된 AccessToken을 갱신하고, 새로운 AccessToken과 RefreshToken을 재발급받는 API입니다."
    )
    @PostMapping("/regenerate")
    public ApiResponse<String> regenerateToken(HttpServletRequest request, HttpServletResponse response) {
        authService.reissueTokens(request, response);
        return ApiResponse.onSuccess("Access & RefreshToken 재발급 완료");
    }
}
