package com.backend.find_crime.utils;


import com.backend.find_crime.config.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final JwtTokenProvider tokenProvider;

    public Long getMemberIdFromRequest(HttpServletRequest request) {
        String token = JwtTokenProvider.resolveToken(request);  // Authorization 헤더에서 토큰 추출
        return tokenProvider.extractMemberId(token);
    }
}
