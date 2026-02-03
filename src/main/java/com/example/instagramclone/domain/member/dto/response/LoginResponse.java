package com.example.instagramclone.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    
    private String message;
    private String username;
    private String accessToken;
    private String profileImage;

    public static LoginResponse of(String message, String username, String accessToken, String profileImage) {
        return LoginResponse.builder()
                .message(message)
                .username(username)
                .accessToken(accessToken)
                .profileImage(profileImage)
                .build();
    }
}
