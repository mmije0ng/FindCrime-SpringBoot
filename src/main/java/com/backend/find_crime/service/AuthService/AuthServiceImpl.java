package com.backend.find_crime.service.AuthService;

import com.backend.find_crime.apiPayload.code.status.ErrorStatus;
import com.backend.find_crime.apiPayload.exception.handler.ErrorHandler;
import com.backend.find_crime.config.properties.Constants;
import com.backend.find_crime.config.security.jwt.JwtTokenProvider;
import com.backend.find_crime.config.security.jwt.RefreshToken;
import com.backend.find_crime.repository.RefreshTokenRepository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    /**
     * Access & Refresh Token 재발급
     */
    @Override
    @Transactional // 토큰 갱신 작업이므로 readOnly=false
    public void reissueTokens(HttpServletRequest request, HttpServletResponse response) {
        // 1. 요청 헤더에서 토큰 추출
        String accessToken = JwtTokenProvider.resolveToken(request);
        String refreshToken = request.getHeader("Refresh-Token"); // RefreshToken은 Bearer prefix 없음

        if (accessToken == null || refreshToken == null) {
            throw new ErrorHandler(ErrorStatus.INVALID_TOKEN);
        }

        // 2. refresh token 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new ErrorHandler(ErrorStatus.INVALID_TOKEN);
        }

        // 3. refresh token에서 사용자 이메일, access token에서 memberId 추출
        String email = jwtTokenProvider.getAuthentication(refreshToken).getName();
        Long memberId;
        try {
            // 만료 안 된 경우
            memberId = jwtTokenProvider.extractMemberId(accessToken);
        } catch (ExpiredJwtException ex) {
            // 만료된 경우에도 claims에서 memberId 추출 가능
            memberId = ex.getClaims().get("memberId", Long.class);
        }

        // 4. 저장된 refresh token과 비교
        RefreshToken savedToken = refreshTokenRepository.findById(email)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.INVALID_TOKEN));

        if (!savedToken.getToken().equals(refreshToken)) {
            throw new ErrorHandler(ErrorStatus.INVALID_TOKEN);
        }

        // 5. 새로운 AccessToken, RefreshToken 발급
        Map<String, String> newTokens = jwtTokenProvider.regenerateAccessTokenAndRotateRefreshToken(email, memberId);

        String newAccessToken = newTokens.get("accessToken");
        String newRefreshToken = newTokens.get("refreshToken");

        log.info("토큰 재발급 완료 - memberId: {}, email: {}", memberId, email);

        // 6. 응답 헤더에 새 토큰 설정
        Constants.setAllTokens(response, newAccessToken, newRefreshToken);
    }
}
