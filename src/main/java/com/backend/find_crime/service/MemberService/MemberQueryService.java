package com.backend.find_crime.service.MemberService;

import com.backend.find_crime.domain.Member;

public interface MemberQueryService {
    // 멤버 존재 검증
    Boolean existsMemberById(Long memberId);

    // 멤버 반환
    Member validateMember(Long memberId);
}
