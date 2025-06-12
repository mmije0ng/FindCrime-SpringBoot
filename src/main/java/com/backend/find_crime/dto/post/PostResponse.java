package com.backend.find_crime.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PostResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "제보게시판 게시글 등록 응답 정보")
    public static class PostCreateResponseDTO {
        @Schema(description = "등록된 게시글 아이디", example = "1")
        Long postId;

        @Schema(description = "게시글 등록 날짜")
        LocalDateTime createdAt;
    }
}
