package com.example.instagramclone.domain.auth.api;


import com.example.instagramclone.domain.member.domain.Member;

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
