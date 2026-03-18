package com.example.instagramclone.domain.member.api;

import com.example.instagramclone.core.common.dto.ApiResponse;
import com.example.instagramclone.domain.member.application.MemberProfileService;
import com.example.instagramclone.infrastructure.security.annotation.LoginUser;
import com.example.instagramclone.infrastructure.security.dto.LoginUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberProfileService memberProfileService;

    /**
     * 특정 유저의 프로필 1건 조회.
     *
     * Day 13 Step 3의 핵심:
     * - 프로필 화면에 필요한 기본 회원 정보
     * - 로그인 유저 기준 isFollowing 상태
     * 를 함께 내려준다.
     */
    @GetMapping("/api/members/{memberId}")
    public ResponseEntity<ApiResponse<MemberProfileResponse>> getMemberProfile(
            @PathVariable Long memberId,
            @LoginUser LoginUserInfoDto loginUser) {
        MemberProfileResponse response = memberProfileService.getProfile(loginUser.id(), memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
