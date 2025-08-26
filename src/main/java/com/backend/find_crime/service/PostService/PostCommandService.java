package com.backend.find_crime.service.PostService;

import com.backend.find_crime.dto.post.PostRequest;
import com.backend.find_crime.dto.post.PostResponse;
import com.backend.find_crime.validation.annotation.ExistMember;

public interface PostCommandService {

    // 제보 게시글 등록
    PostResponse.PostCreateResponseDTO addPost(@ExistMember Long memberId, PostRequest.PostCreateRequestDTO requestDTO);
}
