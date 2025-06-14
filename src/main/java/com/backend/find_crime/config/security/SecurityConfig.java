package com.backend.find_crime.config.security;

import com.backend.find_crime.config.security.jwt.CustomAccessDeniedHandler;
import com.backend.find_crime.config.security.jwt.CustomAuthenticationEntryPoint;
import com.backend.find_crime.config.security.jwt.JwtAuthenticationFilter;
import com.backend.find_crime.config.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity // Spring Security 설정 활성화
@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint; // Unauthorized 핸들러
    private final CustomAccessDeniedHandler customAccessDeniedHandler; // Forbidden 핸들러

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 세션 정책: STATELESS -> 서버는 세션을 생성하지 않음
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // HTTP 요청에 대한 접근 제어 설정
                .authorizeHttpRequests(
                        (requests) -> requests

                                .requestMatchers("/",  "/api/auth/login/kakao", "/auth/login/kakao", "/api/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // permitAll(): 인증 없이 접근 가능한 경로 지정
                                .requestMatchers("/admin/**").hasRole("ADMIN") // hasRole(): 역할을 가진 사용자만 접근 가능하도록 제한
                                .anyRequest().authenticated() // 그 외 모든 요청에 대한 인증 요구
                )
                .csrf()
                .disable()

                // Unauthorized, Forbidden 에러 핸들러 추가
                .exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint))
                .exceptionHandling(exception -> exception.accessDeniedHandler(customAccessDeniedHandler))

                // JWT 인증 필터 등록
                // UsernamePasswordAuthenticationFilter 이전에 커스텀 필터 실행
                // 요청에서 JWT 추출 및 검증 후 SecurityContext에 인증 정보 저장
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 비밀번호 암호황
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}