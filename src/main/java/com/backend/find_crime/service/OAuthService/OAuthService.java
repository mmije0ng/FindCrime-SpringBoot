package com.backend.find_crime.service.OAuthService;

import com.backend.find_crime.dto.member.MemberResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface OAuthService {
    // 카카오 소셜 로그인
    MemberResponse.LoginResultDto kakaoOAuthLoginWithAccessToken(HttpServletRequest request, HttpServletResponse response);
}
