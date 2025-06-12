package com.backend.find_crime.converter;

import com.backend.find_crime.domain.Member;
import com.backend.find_crime.domain.Post;
import com.backend.find_crime.domain.PostLike;
public class PostLikeConverter {

    public static PostLike toPostLike(Member member, Post post) {
        return PostLike.builder()
                .member(member)
                .post(post)
                .build();
    }
}
