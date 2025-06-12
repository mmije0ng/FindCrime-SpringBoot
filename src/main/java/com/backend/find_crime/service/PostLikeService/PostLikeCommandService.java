package com.backend.find_crime.service.PostLikeService;

public interface PostLikeCommandService {
    // 좋아요 등록
    void addPostLike(Long memberId, Long postId);

    // 좋아요 삭제
    void removePostLike(Long memberId, Long postId);
}
