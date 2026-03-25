package com.example.instagramclone.domain.member.api;

import com.example.instagramclone.domain.member.domain.Member;

/**
 * 프로필 1건 조회 응답 DTO.
 *
 * Day 13 Step 3에서는 "프로필 화면 상단에 팔로우 버튼 상태를 어떻게 내려줄까?"
 * 를 보여주는 것이 핵심이므로, 우선은 회원 기본 정보 + isFollowing + isCurrentUser 만 담는다.
 * 팔로워 수 / 팔로잉 수 / 게시물 수 같은 헤더 집계 정보는 Day 15에서 확장한다.
 */
    public record MemberProfileResponse(
            Long memberId,
            String username,
            String name,
            String profileImageUrl,
            boolean isFollowing,
            boolean isCurrentUser
    ) {
    public static MemberProfileResponse of(Member member, boolean isFollowing, boolean isCurrentUser) {
            return new MemberProfileResponse(
                    member.getId(),
                    member.getUsername(),
                    member.getName(),
                    member.getProfileImageUrl(),
                    isFollowing,
                    isCurrentUser
            );
        }
    }
