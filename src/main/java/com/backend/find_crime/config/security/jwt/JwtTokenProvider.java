package com.backend.find_crime.config.security.jwt;

import com.backend.find_crime.apiPayload.code.status.ErrorStatus;
import com.backend.find_crime.apiPayload.exception.handler.ErrorHandler;
import com.backend.find_crime.config.properties.Constants;
import com.backend.find_crime.config.properties.JwtProperties;
import com.backend.find_crime.repository.RefreshTokenRepository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final RefreshTokenRepository refreshTokenRepository;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    // 인증 정보를 받아 JWT Access Token을 생성하고 반환
    public String generateToken(Authentication authentication) {
        String email = authentication.getName();

        return Jwts.builder()
                .setSubject(email) // 이메일을 subject에 저장
                .claim("role", authentication.getAuthorities().iterator().next().getAuthority())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration().getAccess()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 토큰 유효성 검증
    // 토큰 만료, 위조, 형식 오류 등일 때 예외 발생
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException ex) {
            throw ex; // 외부에서 처리하게 던짐
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // JWT 토큰에서 인증 정보를 추출하여 Spring Security의 Authentication 객체로 변환
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        String email = claims.getSubject();
        String role = claims.get("role", String.class);

        // 이메일을 사용하여 인증 객체 생성
        // User 객체의 username 필드에 이메일이 들어가므로, 이후 인증 객체에서 getName() 호출 시 이메일이 반환
        User principal = new User(email, "", Collections.singleton(() -> role));
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    // HTTP 요청의 헤더에서 JWT 토큰을 추출
    public static String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constants.ACCESS_TOKEN_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constants.BEARER_PREFIX)) {
            return bearerToken.substring(Constants.BEARER_PREFIX.length());
        }
        return null;
    }

    // HttpServletRequest 에서 토큰 값을 추출 후
    // getAuthentication 메소드를 이용해서 Spring Security의 Authentication 객체로 변환
    public Authentication extractAuthentication(HttpServletRequest request){
        String accessToken = resolveToken(request);
        if(accessToken == null || !validateToken(accessToken)) {
            throw new ErrorHandler(ErrorStatus.INVALID_TOKEN);
        }
        return getAuthentication(accessToken);
    }

    // RefreshToken 생성
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration().getRefresh()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // RefreshToken 저장
    public void storeRefreshToken(String email, String refreshToken) {
         refreshTokenRepository.save(
                RefreshToken.builder()
                        .email(email)
                        .token(refreshToken)
                        .build()
        );
    }

    // RefreshToken 삭제
    public void deleteRefreshToken(String email) {
        refreshTokenRepository.deleteById(email);
    }

    // AccessToken 생성 및 RefreshToken 회전 (기존 토큰 삭제 후 재발급)
    // AccessToken과 RefreshToken을 모두 반환
    public Map<String, String> regenerateAccessTokenAndRotateRefreshToken(String email) {
        // 기존 RefreshToken 삭제
        deleteRefreshToken(email);

        // 새로운 RefreshToken 발급 및 저장
        String newRefreshToken = generateRefreshToken(email);
        storeRefreshToken(email, newRefreshToken);

        // 새로운 AccessToken 발급
        Authentication auth = new UsernamePasswordAuthenticationToken(
                email, null, Collections.singleton(() -> "ROLE_USER")
        );
        String newAccessToken = generateToken(auth);

        // AccessToken, RefreshToken 함께 반환
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        tokens.put("refreshToken", newRefreshToken);

        return tokens;
    }
}
