package com.backend.find_crime.config.properties;

import jakarta.servlet.http.HttpServletResponse;

public final class Constants {
    public static final String ACCESS_TOKEN_HEADER = "Authorization";
    private static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    public static final String BEARER_PREFIX = "Bearer ";

    // Access Token만 응답 헤더에 설정
    public static void setAccessToken(HttpServletResponse response, String accessToken) {
        if (accessToken != null && !accessToken.isBlank()) {
            response.setHeader(ACCESS_TOKEN_HEADER, BEARER_PREFIX + accessToken);
        }
    }

    // Refresh Token만 응답 헤더에 설정
    public static void setRefreshToken(HttpServletResponse response, String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            response.setHeader(REFRESH_TOKEN_HEADER, refreshToken);
        }
    }

    // Access Token과 Refresh Token 모두 설정
    public static void setAllTokens(HttpServletResponse response, String accessToken, String refreshToken) {
        setAccessToken(response, accessToken);
        setRefreshToken(response, refreshToken);
    }
}