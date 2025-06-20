package com.backend.find_crime.dto.post;

import com.backend.find_crime.validation.annotation.ExistMember;
import com.backend.find_crime.validation.annotation.ValidatePage;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PostRequest {

    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "제보게시판 게시글 등록 요청 정보")
    public static class PostCreateRequestDTO {
        @Schema(description = "게시글 제목")
        @NotNull
        @Size(min = 1, max = 20)
        private String postTitle;

        @NotNull
        @Size(min = 1, max = 200)
        @Schema(description = "게시글 내용")
        private String postContent;

//        @Schema(description = "게시글 이미지 URL")
//        private String postImageUrl;

        @Schema(description = "지역 이름", example = "서울")
        @NotNull
        private String areaName;

        @Schema(description = "지역 디테일 이름", example = "성북구")
        @NotNull
        private String areaDetailName;

        @Schema(description = "범죄대분류", example = "지능범죄")
        @NotNull
        private String crimeType;

        @Schema(description = "범죄소분류", example = "사기")
        @NotNull
        private String crimeDetailType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @Schema(description = "제보게시판 게시글 페이지네이션 카테고리 요청 정보")
    public static class PostPageRequestDTO {

        @Schema(description = "멤버 아이디", example = "1")
        @ExistMember
        Long memberId;

        @Schema(description = "지역 이름", example = "서울")
        @NotNull
        private String areaName;

        @Schema(description = "지역 디테일 이름", example = "성북구")
        @NotNull
        private String areaDetailName;

        @Schema(description = "범죄대분류", example = "지능범죄")
        @NotNull
        private String crimeType;

        @Schema(description = "범죄소분류", example = "사기")
        @NotNull
        private String crimeDetailType;

//        @Schema(description = "페이지 번호", example = "1")
//        @ValidatePage
//        Integer page;
    }
}
