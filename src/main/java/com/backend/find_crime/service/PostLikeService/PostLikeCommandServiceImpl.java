package com.backend.find_crime.service.PostLikeService;

import com.backend.find_crime.apiPayload.code.status.ErrorStatus;
import com.backend.find_crime.apiPayload.exception.handler.ErrorHandler;
import com.backend.find_crime.converter.PostLikeConverter;
import com.backend.find_crime.domain.Member;
import com.backend.find_crime.domain.Post;
import com.backend.find_crime.domain.PostLike;
import com.backend.find_crime.repository.PostLikeRepository.PostLikeRepository;
import com.backend.find_crime.service.MemberService.MemberQueryService;
import com.backend.find_crime.service.PostService.PostQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostLikeCommandServiceImpl implements PostLikeCommandService {

    private final PostLikeRepository postLikeRepository;
    private final MemberQueryService memberQueryService;
    private final PostQueryService postQueryService;

    // 좋아요 등록
    @Transactional
    @Override
    public void addPostLike(Long memberId, Long postId) {
        Member member = memberQueryService.validateMember(memberId);
        Post post = postQueryService.validatePost(postId);

        PostLike postLike = PostLikeConverter.toPostLike(member, post);
        postLikeRepository.save(postLike);

        log.info("좋아요 등록 완료 postLikeId: {}", postLike.getId());
    }

    // 좋아요 삭제
    @Transactional
    @Override
    public void removePostLike(Long memberId, Long postId) {
        PostLike postLike = postLikeRepository.findByPostIdAndMemberId(postId, memberId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.POST_LIKE_NOT_FOUND));
        Long postLikeId = postLike.getId();

        postLikeRepository.delete(postLike);

        log.info("좋아요 삭제 완료 postLikeId: {}", postLikeId);
    }
}
