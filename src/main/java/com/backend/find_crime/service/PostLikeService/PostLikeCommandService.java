package com.backend.find_crime.service.PostLikeService;

import com.backend.find_crime.validation.annotation.ExistMember;

public interface PostLikeCommandService {
    // 좋아요 등록
    void addPostLike(@ExistMember Long memberId, Long postId);

    // 좋아요 삭제
    void removePostLike(@ExistMember Long memberId, Long postId);
}
