package com.backend.find_crime.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class MemberResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "로그인 응답 정보")
    public static class LoginResultDto {
        @Schema(description = "로그인에 성공한 유저 아이디", example = "1")
        Long userId;

        // accessToken은 response header에
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "마이페이지 응답 정보")
    public static class  MyPageResultDto {
        String email;
        String nickName;
        String profileImageUrl;
        LocalDateTime createdAt;
    }
}
