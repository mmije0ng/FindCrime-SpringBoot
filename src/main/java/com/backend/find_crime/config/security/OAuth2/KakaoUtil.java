package com.backend.find_crime.config.security.OAuth2;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoUtil {

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;

    // 카카오 토큰 요청용 WebClient
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://kauth.kakao.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .build();

    /**
     * 인가 코드(code)를 이용해 액세스 토큰을 요청하는 함수
     * @param code 카카오에서 리디렉션 시 전달하는 인가 코드
     * @return KakaoDTO.OAuthToken - 액세스 토큰 등 토큰 정보가 담긴 객체
     */
    public KakaoDTO.OAuthToken requestToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        return webClient.post()
                .uri("/oauth/token")
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(KakaoDTO.OAuthToken.class)
                .onErrorResume(e -> {
                    log.error("Failed to get Kakao token: {}", e.getMessage());
                    return Mono.empty();
                })
                .block();
    }

    /**
     * 액세스 토큰을 이용해 사용자 정보 요청
     * @param accessToken 카카오로부터 발급받은 액세스 토큰
     * @return KakaoDTO.KakaoProfile - 사용자 프로필 정보
     */
    public KakaoDTO.KakaoProfile requestUserInfo(String accessToken) {
        WebClient userClient = WebClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .build();

        return userClient.get()
                .uri("/v2/user/me")
                .retrieve()
                .bodyToMono(KakaoDTO.KakaoProfile.class)
                .onErrorResume(e -> {
                    log.error("Failed to get Kakao user info: {}", e.getMessage());
                    return Mono.empty();
                })
                .block();
    }
}
