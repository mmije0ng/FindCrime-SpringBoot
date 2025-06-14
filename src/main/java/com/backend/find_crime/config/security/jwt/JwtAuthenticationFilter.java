package com.backend.find_crime.config.security.jwt;

import com.backend.find_crime.apiPayload.ApiResponse;
import com.backend.find_crime.apiPayload.code.status.ErrorStatus;
import com.backend.find_crime.config.properties.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper(); // 재사용을 위해 필드로

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 요청 헤더에서 JWT 토큰 추출
            String token = resolveToken(request);

            // 토큰이 있고, 유효하다면 인증 객체 생성 및 등록
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            // 다음 필터로 요청 전달
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException ex) {
            // 만료된 JWT 토큰 예외 발생 시 custom 403 응답 반환
            log.error("JWT Token 만료: {}", ex.getMessage());

            // JSON 응답 설정
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // API 응답 객체 생성
            ApiResponse<Void> errorResponse = ApiResponse.onFailure(
                    ErrorStatus.EXPIRED_TOKEN.getCode(),
                    ErrorStatus.EXPIRED_TOKEN.getMessage(),
                    null
            );

            // JSON 응답 반환
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }
    }

    // "Authorization" 헤더에서 "Bearer " 접두사를 제거한 순수 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constants.ACCESS_TOKEN_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constants.BEARER_PREFIX)) {
            return bearerToken.substring(Constants.BEARER_PREFIX.length());
        }
        return null;
    }

    // 재발급 API 경로는 JWT 필터를 타지 않도록 예외 처리
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/api/auth/regenerate");
    }
}
