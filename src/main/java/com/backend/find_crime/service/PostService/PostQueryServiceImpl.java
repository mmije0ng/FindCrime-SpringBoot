package com.backend.find_crime.service.PostService;

import com.backend.find_crime.apiPayload.code.status.ErrorStatus;
import com.backend.find_crime.apiPayload.exception.handler.ErrorHandler;
import com.backend.find_crime.converter.PostConverter;
import com.backend.find_crime.domain.Post;
import com.backend.find_crime.domain.mapping.CrimeArea;
import com.backend.find_crime.dto.post.PostRequest;
import com.backend.find_crime.dto.post.PostResponse;
import com.backend.find_crime.repository.CrimeAreaRepository.CrimeAreaRepository;
import com.backend.find_crime.repository.PostLikeRepository.PostLikeRepository;
import com.backend.find_crime.repository.PostRepository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostQueryServiceImpl implements PostQueryService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CrimeAreaRepository crimeAreaRepository;
    private static final Integer PAGE_SIZE=10;

    // 페이지 정렬, 최신순
    private Pageable pageRequest(Integer pageNumber) {
        return PageRequest.of(pageNumber, PAGE_SIZE, Sort.by("createdAt").descending());
    }

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
    public PostResponse.PostDetailResponseDTO findPostDetail(Long postId, Long memberId) {
        Post post = validatePost(postId);
        Boolean isLiked = postLikeRepository.findByPostIdAndMemberId(postId, memberId).isPresent();

        log.info("제보 게시글 상세 조회 완료 postId: {}", postId);
        return PostConverter.toPostDetailResponseDTO(post, post.getMember(), isLiked);
    }

    // 카테고리별 제보 게시글 페이지네이션 조회
    @Override
    public PostResponse.PostInfoPageDTO findPostInfoPage(PostRequest.PostPageRequestDTO requestDTO, Integer page) {
        // 범죄지역 조회
        CrimeArea crimeArea = crimeAreaRepository.findWithCrimeAndArea(
                requestDTO.getCrimeType(),
                requestDTO.getCrimeDetailType(),
                requestDTO.getAreaName(),
                requestDTO.getAreaDetailName()
        ).orElseThrow(() -> {
            return new ErrorHandler(ErrorStatus.CRIME_AREA_NOT_FOUND_BY_CRIME_AND_AREA);
        });

        // 카테고리별 제보 게시글 페이지네이션 조회
        Page<Post> postPage = postRepository.findPostPagesByCrimeArea(crimeArea, pageRequest(page));

        log.info("제보 게시글 페이지네이션 조회 완료");
        return PostConverter.toPostInfoPageDTO(postPage);
    }
}
