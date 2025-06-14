package com.backend.find_crime.converter;

import com.backend.find_crime.domain.Member;
import com.backend.find_crime.domain.enums.Role;
import com.backend.find_crime.domain.enums.SocialType;
import com.backend.find_crime.dto.member.MemberResponse;

public class MemberConverter {

    // 멤버 생성
    public static Member toMember(String email, String nickname, String profileImage) {
        return Member.builder()
                .email(email)
                .nickName(nickname)
                .profileImage(profileImage)
                .role(Role.USER)
                .socialType(SocialType.KAKAO) // 일단 카카오만
                .build();
    }

    // 로그인 응답
    public static MemberResponse.LoginResultDto toLoginResultDto(Long memberId) {
        return MemberResponse.LoginResultDto.builder().userId(memberId).build();
    }

    // 마이페이지
    public static MemberResponse.MyPageResultDto toMyPageResultDto(Member member) {
        return MemberResponse.MyPageResultDto.builder()
                .email(member.getEmail())
                .nickName(member.getNickName())
                .profileImageUrl(member.getProfileImage())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
