package com.backend.find_crime.controller;

import com.backend.find_crime.apiPayload.ApiResponse;
import com.backend.find_crime.dto.member.MemberResponse;
import com.backend.find_crime.service.MemberService.MemberQueryService;
import com.backend.find_crime.validation.annotation.ExistMember;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "멤버", description = "멤버 API")
@RequiredArgsConstructor
@RequestMapping("/api/member")
@RestController
public class MemberController {

    private final MemberQueryService memberQueryService;

    @Operation(summary = "마이페이지 조회 API")
    @GetMapping("/{memberId}")
    public ApiResponse<MemberResponse.MyPageResultDto> getMemberInfo(@PathVariable @ExistMember Long memberId) {
        return ApiResponse.onSuccess(memberQueryService.findMemberInfo(memberId));
    }
}
