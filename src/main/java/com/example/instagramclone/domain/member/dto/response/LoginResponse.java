package com.example.instagramclone.domain.member.dto.response;

import com.example.instagramclone.domain.member.entity.Member;

public record LoginResponse(
        AuthTokens tokens,
        UserInfoDto user
) {
    public static LoginResponse of(AuthTokens tokens, Member member) {
        return new LoginResponse(tokens, UserInfoDto.from(member));
    }

    public record UserInfoDto(
            Long id,
            String username,
            String name,
            String profileImageUrl
    ) {
        public static UserInfoDto from(Member member) {
            return new UserInfoDto(
                    member.getId(),
                    member.getUsername(),
                    member.getName(),
                    member.getProfileImageUrl()
            );
        }
    }
}
