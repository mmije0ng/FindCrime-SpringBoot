package com.backend.find_crime.controller;

import com.backend.find_crime.apiPayload.ApiResponse;
import com.backend.find_crime.dto.member.MemberResponse;
import com.backend.find_crime.service.MemberService.MemberQueryService;
import com.backend.find_crime.utils.AuthUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "멤버", description = "멤버 API")
@RequiredArgsConstructor
@RequestMapping("/api/member")
@RestController
public class MemberController {

    private final MemberQueryService memberQueryService;
    private final AuthUtil authUtil;

    @Operation(summary = "마이페이지 조회 API")
    @GetMapping()
    public ApiResponse<MemberResponse.MyPageResultDto> getMemberInfo(HttpServletRequest request) {
        Long memberId = authUtil.getMemberIdFromRequest(request);
        log.info(memberId.toString());
        return ApiResponse.onSuccess(memberQueryService.findMemberInfo(memberId));
    }
}
