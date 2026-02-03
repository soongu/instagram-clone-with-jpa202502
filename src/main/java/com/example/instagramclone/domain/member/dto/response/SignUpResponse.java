package com.example.instagramclone.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpResponse {

    private String message;
    private String username;

    public static SignUpResponse of(String message, String username) {
        return SignUpResponse.builder()
                .message(message)
                .username(username)
                .build();
    }
}
