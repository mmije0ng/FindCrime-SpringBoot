package com.backend.find_crime.service.PostService;

import com.backend.find_crime.dto.post.PostRequest;
import com.backend.find_crime.dto.post.PostResponse;

public interface PostCommandService {

    // 제보 게시글 등록
    PostResponse.PostCreateResponseDTO addPost(Long memberId, PostRequest.PostCreateRequestDTO requestDTO);
}
