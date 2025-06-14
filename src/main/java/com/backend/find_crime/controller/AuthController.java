package com.backend.find_crime.controller;

import com.backend.find_crime.apiPayload.ApiResponse;
import com.backend.find_crime.dto.member.MemberResponse;
import com.backend.find_crime.service.OAuthService.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "로그인/회원가입", description = "인증에 관한 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final OAuthService oAuthService;

    // 카카오 로그인 API
    @Operation(summary = "카카오 로그인 API",description = "카카오 소셜 로그인 API입니다.")
    @PostMapping("/login/kakao")
    public ApiResponse<MemberResponse.LoginResultDto> kakaoOAuthLogin(HttpServletRequest request,
                                                                      HttpServletResponse response) {
        return ApiResponse.onSuccess(oAuthService.kakaoOAuthLoginWithAccessToken(request, response));
    }
}
