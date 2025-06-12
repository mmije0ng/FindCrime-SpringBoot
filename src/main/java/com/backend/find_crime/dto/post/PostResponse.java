package com.backend.find_crime.dto.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "제보 게시판 게시글 등록 응답 정보")
    public static class PostCreateResponseDTO {
        @Schema(description = "등록된 게시글 아이디", example = "1")
        Long postId;

        @Schema(description = "게시글 등록 날짜")
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "제보 게시판 게시글 조회 응답 정보")
    public static class PostDetailResponseDTO {
        String postTitle;
        String postContent;
        Boolean isLiked;
        LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "제보 게시판 페이지네이션 응답 정보")
    public static class PostInfoPageDTO {

        @Schema(description = "게시글 목록")
        private List<PostInfoDTO> postList;

        @Schema(description = "총 페이지 수", example = "5")
        private Integer totalPage;

        @Schema(description = "총 미션 개수", example = "50")
        private Long totalElements;

        @Schema(description = "첫 페이지 여부", example = "true")
        private Boolean isFirst;

        @Schema(description = "마지막 페이지 여부", example = "false")
        private Boolean isLast;

        @Schema(description = "현재 페이지의 제보 게시글 수", example = "10")
        private Integer postListSize;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "제보 게시판 페이지네이션용 게시글 응답 정보")
    public static class PostInfoDTO {
        private Long postId;
        private String postTitle;
        private LocalDateTime createdAt;
    }
}
