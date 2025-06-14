package com.backend.find_crime.service.MemberService;

import com.backend.find_crime.apiPayload.code.status.ErrorStatus;
import com.backend.find_crime.apiPayload.exception.handler.ErrorHandler;
import com.backend.find_crime.converter.MemberConverter;
import com.backend.find_crime.domain.Member;
import com.backend.find_crime.dto.member.MemberResponse;
import com.backend.find_crime.repository.MemberRepository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberQueryServiceImpl implements MemberQueryService {

    private final MemberRepository memberRepository;

    // 멤버 존재 검증
    @Override
    public Boolean existsMemberById(Long memberId) {
        return memberRepository.findById(memberId).isPresent();
    }

    // 멤버 반환
    @Override
    public Member validateMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ErrorHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    // 마이페이지
    @Override
    public MemberResponse.MyPageResultDto findMemberInfo(Long memberId) {
        Member member = validateMember(memberId);
        return MemberConverter.toMyPageResultDto(member);
    }
}
