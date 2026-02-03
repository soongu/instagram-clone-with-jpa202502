package com.example.instagramclone.domain.member.dto.response;

import com.example.instagramclone.domain.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberWithStatsDto {
    private Member member;
    private long feedCount;
    private long followerCount; // 나를 팔로우하는 사람 수 (Target ID = Me)
    private long followingCount; // 내가 팔로우하는 사람 수 (Source ID = Me)
    private boolean isFollowing;
}
