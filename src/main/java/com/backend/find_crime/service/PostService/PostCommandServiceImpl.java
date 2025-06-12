package com.backend.find_crime.service.PostService;

import com.backend.find_crime.apiPayload.code.status.ErrorStatus;
import com.backend.find_crime.apiPayload.exception.handler.ErrorHandler;
import com.backend.find_crime.controller.PostController;
import com.backend.find_crime.converter.PostConverter;
import com.backend.find_crime.domain.Member;
import com.backend.find_crime.domain.Post;
import com.backend.find_crime.domain.mapping.CrimeArea;
import com.backend.find_crime.dto.post.PostRequest;
import com.backend.find_crime.dto.post.PostResponse;
import com.backend.find_crime.repository.CrimeAreaRepository.CrimeAreaRepository;
import com.backend.find_crime.repository.PostRepository.PostRepository;
import com.backend.find_crime.service.MemberService.MemberQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class PostCommandServiceImpl implements PostCommandService {

    private final PostRepository postRepository;
    private final MemberQueryService memberQueryService;
    private final CrimeAreaRepository crimeAreaRepository;

    // 제보 게시글 등록
    @Transactional
    @Override
    public PostResponse.PostCreateResponseDTO addPost(Long memberId, PostRequest.PostCreateRequestDTO requestDTO) {
        // 멤버 조회
        Member member = memberQueryService.validateMember(memberId);

        // 범죄지역 조회
        CrimeArea crimeArea = crimeAreaRepository.findWithCrimeAndArea(
                requestDTO.getCrimeType(),
                requestDTO.getCrimeDetailType(),
                requestDTO.getAreaName(),
                requestDTO.getAreaDetailName()
        ).orElseThrow(() -> {
            return new ErrorHandler(ErrorStatus.CRIME_AREA_NOT_FOUND_BY_CRIME_AND_AREA);
        });

        // 게시글 저장
        Post post = PostConverter.toPost(requestDTO, member, crimeArea);
        postRepository.save(post);

        log.info("게시글 등록 완료, postId: {}", post.getId());
        return PostConverter.toPostCreateResponseDTO(post);
    }
}
