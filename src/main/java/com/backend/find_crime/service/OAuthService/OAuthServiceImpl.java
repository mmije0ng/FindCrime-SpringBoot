package com.backend.find_crime.service.OAuthService;

import com.backend.find_crime.config.properties.Constants;
import com.backend.find_crime.config.security.OAuth2.KakaoDTO;
import com.backend.find_crime.config.security.OAuth2.KakaoUtil;
import com.backend.find_crime.config.security.jwt.JwtTokenProvider;
import com.backend.find_crime.converter.MemberConverter;
import com.backend.find_crime.domain.Member;
import com.backend.find_crime.dto.member.MemberResponse;
import com.backend.find_crime.repository.MemberRepository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class OAuthServiceImpl implements OAuthService {

    private final KakaoUtil kakaoUtil;
    private final MemberRepository memberRepository;
    private final JwtTokenProvider jwtTokenProvider;

    // 카카오 소셜 로그인
    @Transactional
    @Override
    public MemberResponse.LoginResultDto kakaoOAuthLoginWithAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // 1. 클라이언트로부터 전달받은 Access Token으로 사용자 정보 조회
        String accessTokenFromClient = JwtTokenProvider.resolveToken(request);
        KakaoDTO.KakaoProfile kakaoProfile = kakaoUtil.requestUserInfo(accessTokenFromClient);
        if (kakaoProfile == null) throw new RuntimeException("카카오 사용자 정보 요청 실패");

        String email = kakaoProfile.getKakao_account().getEmail();
        String nickname = kakaoProfile.getKakao_account().getProfile().getNickname();
        String profileImage = kakaoProfile.getKakao_account().getProfile().getProfile_image_url();

        // 2. 사용자 존재 여부 확인 → 없으면 자동 회원가입
        AtomicBoolean isNewMember = new AtomicBoolean(false);

        Member member = memberRepository.findByEmail(email).orElseGet(() -> {
            isNewMember.set(true);
            Member newMember = MemberConverter.toMember(email, nickname, profileImage);
            return memberRepository.save(newMember);
        });

        // 3. JWT 토큰 생성 및 헤더에 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                member.getEmail(), null,
                Collections.singleton(() -> member.getRole().name())
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getEmail());
        jwtTokenProvider.storeRefreshToken(member.getEmail(), refreshToken);
        Constants.setAllTokens(response, accessToken, refreshToken);

        log.info("카카오 로그인 완료 (accessToken 직접 전달), userId: {}", member.getId());
        return MemberConverter.toLoginResultDto(member.getId());
    }

}
