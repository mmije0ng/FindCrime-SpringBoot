package com.backend.find_crime.controller;

import com.backend.find_crime.apiPayload.ApiResponse;
import com.backend.find_crime.service.PostLikeService.PostLikeCommandService;
import com.backend.find_crime.validation.annotation.ExistMember;

import com.backend.find_crime.validation.annotation.ExistPost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "좋아요 게시판", description = "제포 게시판 좋아요에 관한 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/post/like")
public class PostLikeController {

    private final PostLikeCommandService postLikeCommandService;

    @Operation(summary = "제보 게시판 게시글 좋아요 등록 API")
    @PostMapping("/{memberId}/{postId}")
    public ApiResponse<String> postLike(@PathVariable @ExistMember Long memberId, @PathVariable @ExistPost Long postId) {
        postLikeCommandService.addPostLike(memberId, postId);
        return ApiResponse.onSuccess("좋아요 등록 완료");
    }

    @Operation(summary = "제보 게시판 게시글 좋아요 삭제 API")
    @DeleteMapping("/{memberId}/{postId}")
    public ApiResponse<?> deletePostLike(@PathVariable @ExistMember Long memberId, @PathVariable @ExistPost Long postId) {
        postLikeCommandService.removePostLike(memberId, postId);
        return ApiResponse.onSuccess("좋아요 삭제 완료");
    }
}
