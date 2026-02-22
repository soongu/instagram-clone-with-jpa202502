package com.example.instagramclone.domain.member.dto.response;

import com.example.instagramclone.domain.member.entity.MemberRole;
import lombok.Builder;

@Builder
public record SessionUser(
        Long id,
        String username,
        String name,
        String profileImageUrl,
        MemberRole role
) {
    public static SessionUser from(com.example.instagramclone.domain.member.entity.Member member) {
        return SessionUser.builder()
                .id(member.getId())
                .username(member.getUsername())
                .name(member.getName())
                .profileImageUrl(member.getProfileImageUrl())
                .role(member.getRole())
                .build();
    }
}
