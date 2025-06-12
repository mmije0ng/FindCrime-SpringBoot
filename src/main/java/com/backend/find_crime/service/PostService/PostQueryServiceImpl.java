package com.backend.find_crime.service.PostService;

import com.backend.find_crime.apiPayload.code.status.ErrorStatus;
import com.backend.find_crime.apiPayload.exception.handler.ErrorHandler;
import com.backend.find_crime.converter.PostConverter;
import com.backend.find_crime.domain.Post;
import com.backend.find_crime.dto.post.PostResponse;
import com.backend.find_crime.repository.PostLikeRepository.PostLikeRepository;
import com.backend.find_crime.repository.PostRepository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostQueryServiceImpl implements PostQueryService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Override
    public Boolean existsPostById(Long postId) {
        return postRepository.findById(postId).isPresent();
    }

    @Override
    public Post validatePost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.POST_NOT_FOUND));
    }

    // 제보 게시글 상세 조회
    @Override
    public PostResponse.PostDetailResponseDTO findPostDetail(Long memberId, Long postId) {
        Post post = validatePost(postId);
        Boolean isLiked = postLikeRepository.findByPostIdAndMemberId(postId, memberId).isPresent();

        return PostConverter.toPostDetailResponseDTO(post, isLiked);
    }
}
