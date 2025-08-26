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
    private final CustomAccessDeniedHandler customAccessDeniedHandler;     // Forbidden 핸들러

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {}) // ✅ WebMvcConfigurer(WebConfig)의 CORS 설정을 반영하도록 허용
                .csrf((auth) -> auth.disable()) // 필요 시 CSRF 보호 비활성화
                .formLogin((auth) -> auth.disable()) // form 로그인 방식 disable
                .httpBasic((auth) -> auth.disable()) // http Basic 인증 방식 disable
                // 세션 정책: STATELESS -> 서버는 세션을 생성하지 않음
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // HTTP 요청에 대한 접근 제어 설정
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(
                                "/api/auth/login/kakao",
                                "/auth/login/kakao",
                                "/api/auth/regenerate",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/test"
                        ).permitAll()
                        .requestMatchers(
                                "/admin/**",
                                "/api/crime-areas/map-all",
                                "/api/statistics/upload"
                        ).hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                // Unauthorized, Forbidden 에러 핸들러 추가
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                // JWT 인증 필터 등록
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider);
    }
}
