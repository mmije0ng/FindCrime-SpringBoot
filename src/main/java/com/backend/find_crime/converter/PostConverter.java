package com.backend.find_crime.converter;

import com.backend.find_crime.domain.Member;
import com.backend.find_crime.domain.Post;
import com.backend.find_crime.domain.mapping.CrimeArea;
import com.backend.find_crime.dto.post.PostRequest;
import com.backend.find_crime.dto.post.PostResponse;

public class PostConverter {

    public static Post toPost(PostRequest.PostCreateRequestDTO dto, Member member, CrimeArea crimeArea) {
        return Post.builder()
                .postTitle(dto.getPostTitle())
                .postContent(dto.getPostContent())
                .member(member)
                .crimeArea(crimeArea)
                .build();
    }

    public static PostResponse.PostCreateResponseDTO toPostCreateResponseDTO(Post post) {
        return PostResponse.PostCreateResponseDTO.builder()
                .postId(post.getId())
                .createdAt(post.getCreatedAt()) // createdAt 필드는 Post 엔티티에 있어야 함
                .build();
    }

    public static PostResponse.PostDetailResponseDTO toPostDetailResponseDTO(Post post, Boolean isLiked) {
        return PostResponse.PostDetailResponseDTO.builder()
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .isLiked(isLiked)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
