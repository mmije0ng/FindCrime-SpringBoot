package com.backend.find_crime.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// 전역적으로 yml 파일에서 JWT 설정값을 가져옴
@Component
@Getter
@Setter
@ConfigurationProperties("spring.security.jwt.token")
public class JwtProperties {
    private String secretKey="";
    private Expiration expiration;

    @Getter
    @Setter
    public static class Expiration{
        private Long access;
        private Long refresh;
    }
}