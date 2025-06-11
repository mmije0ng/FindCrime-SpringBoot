package com.backend.find_crime.service.DiscordService;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DiscordService {

    @Value("${discord.web_hook_url}")
    private String webhookUrl;

    private final Environment environment; // 프로필 정보 주입

    public void sendErrorToDiscord(Exception e, WebRequest request) {
        try {
            // local 프로필이면 전송하지 않음
            if (isLocalProfile()) {
                log.info("현재 환경은 'local'입니다. 디스코드 알림 전송을 생략합니다.");
                return;
            }

            if (webhookUrl == null || webhookUrl.isBlank()) return;

            String url = "";
            if (request instanceof ServletWebRequest servletWebRequest) {
                url = servletWebRequest.getRequest().getRequestURI();
            }

            String message = String.format("""
                🚨 **500 Internal Server Error 발생**
                - 발생 시간: %s
                - 요청 URL: %s
                - 예외 메시지: %s
                """, LocalDateTime.now(), url, e.toString()
            );

            Map<String, String> payload = new HashMap<>();
            payload.put("content", message);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new ObjectMapper().writeValueAsString(payload)))
                    .build();

            client.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());

            log.info("디스코드 500 알림 전송 성공");

        } catch (Exception ex) {
            log.error("디스코드 500 알림 전송 실패: {}", ex.getMessage());
        }
    }

    private boolean isLocalProfile() {
        for (String profile : environment.getActiveProfiles()) {
            if (profile.equalsIgnoreCase("local")) {
                return true;
            }
        }
        return false;
    }
}
