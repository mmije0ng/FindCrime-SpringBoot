package com.backend.find_crime.controller;

import com.backend.find_crime.apiPayload.ApiResponse;
import com.backend.find_crime.dto.post.PostRequest;
import com.backend.find_crime.dto.post.PostResponse;
import com.backend.find_crime.service.PostService.PostCommandService;
import com.backend.find_crime.service.PostService.PostQueryService;
import com.backend.find_crime.validation.annotation.ExistMember;
import com.backend.find_crime.validation.annotation.ExistPost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "제보 게시판", description = "제포 게시판에 관한 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;

    @Operation(summary = "제보 게시판 게시글 등록 API")
    @PostMapping("/{memberId}")
    public ApiResponse<PostResponse.PostCreateResponseDTO> post(@PathVariable @ExistMember Long memberId, @RequestBody PostRequest.PostCreateRequestDTO requestDTO) {
        return ApiResponse.onSuccess(postCommandService.addPost(memberId, requestDTO));
    }

    @Operation(summary = "제보 게시글 상세 조회 API")
    @GetMapping
    public ApiResponse<PostResponse.PostDetailResponseDTO> getPostDetail(@RequestParam @ExistMember Long memberId, @RequestParam @ExistPost Long postId) {
        return ApiResponse.onSuccess(postQueryService.findPostDetail(memberId, postId));
    }
}
