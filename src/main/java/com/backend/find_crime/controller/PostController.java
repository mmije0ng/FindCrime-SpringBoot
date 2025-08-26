package com.backend.find_crime.controller;

import com.backend.find_crime.apiPayload.ApiResponse;
import com.backend.find_crime.dto.post.PostRequest;
import com.backend.find_crime.dto.post.PostResponse;
import com.backend.find_crime.service.PostService.PostCommandService;
import com.backend.find_crime.service.PostService.PostQueryService;
import com.backend.find_crime.utils.AuthUtil;
import com.backend.find_crime.validation.annotation.ExistMember;
import com.backend.find_crime.validation.annotation.ExistPost;
import com.backend.find_crime.validation.annotation.ValidatePage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "제보 게시판", description = "제포 게시판에 관한 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/post")
public class PostController {

    private final PostQueryService postQueryService;
    private final PostCommandService postCommandService;
    private final AuthUtil authUtil;

    @Operation(summary = "제보 게시판 게시글 등록 API")
    @PostMapping
    public ApiResponse<PostResponse.PostCreateResponseDTO> post(HttpServletRequest request, @RequestBody PostRequest.PostCreateRequestDTO requestDTO) {
        Long memberId = authUtil.getMemberIdFromRequest(request);
        return ApiResponse.onSuccess(postCommandService.addPost(memberId, requestDTO));
    }

    @Operation(summary = "제보 게시글 상세 조회 API")
    @GetMapping("/{postId}")
    public ApiResponse<PostResponse.PostDetailResponseDTO> getPostDetail(HttpServletRequest request, @PathVariable @ExistPost Long postId) {
        Long memberId = authUtil.getMemberIdFromRequest(request);
        return ApiResponse.onSuccess(postQueryService.findPostDetail(postId, memberId));
    }

    @Operation(summary = "제보 게시글 페이지네이션 조회 API")
    @GetMapping
    public ApiResponse<PostResponse.PostInfoPageDTO> getPostPages(HttpServletRequest request,
                                                                  PostRequest.PostPageRequestDTO requestDTO,
                                                                  @ValidatePage Integer page) {
        log.info("제보 게시글 페이지네이션 조회");
        return ApiResponse.onSuccess(postQueryService.findPostInfoPage(requestDTO, page));
    }
}
