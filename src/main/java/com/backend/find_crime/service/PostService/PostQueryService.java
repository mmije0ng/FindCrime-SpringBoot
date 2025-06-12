package com.backend.find_crime.service.PostService;


import com.backend.find_crime.domain.Post;
import com.backend.find_crime.dto.post.PostResponse;

public interface PostQueryService {
    // 제보 게시글 존재 검증
    Boolean existsPostById(Long postId);

    // 제보 게시글 반환
    Post validatePost(Long postId);

    // 제보 게시글 상세 조회
    PostResponse.PostDetailResponseDTO findPostDetail(Long memberId, Long postId);
}
