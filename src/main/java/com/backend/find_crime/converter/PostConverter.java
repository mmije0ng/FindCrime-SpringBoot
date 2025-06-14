package com.backend.find_crime.converter;

import com.backend.find_crime.domain.Member;
import com.backend.find_crime.domain.Post;
import com.backend.find_crime.domain.mapping.CrimeArea;
import com.backend.find_crime.dto.post.PostRequest;
import com.backend.find_crime.dto.post.PostResponse;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

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

    public static PostResponse.PostDetailResponseDTO toPostDetailResponseDTO(Post post, Member member, Boolean isLiked) {
        return PostResponse.PostDetailResponseDTO.builder()
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .isLiked(isLiked)
                .nickName(member.getNickName())
                .profileImageUrl(member.getProfileImage())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static PostResponse.PostInfoPageDTO toPostInfoPageDTO(Page<Post> postPage) {
        List<PostResponse.PostInfoDTO> postList = postPage.getContent().stream()
                .map(PostConverter::toPostInfoDTO)
                .collect(Collectors.toList());

        return PostResponse.PostInfoPageDTO.builder()
                .totalPage(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .isFirst(postPage.isFirst())
                .isLast(postPage.isLast())
                .postList(postList)
                .postListSize(postPage.getNumberOfElements())
                .build();
    }

    public static PostResponse.PostInfoDTO toPostInfoDTO(Post post) {
        return PostResponse.PostInfoDTO.builder()
                .postId(post.getId())
                .postTitle(post.getPostTitle())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
