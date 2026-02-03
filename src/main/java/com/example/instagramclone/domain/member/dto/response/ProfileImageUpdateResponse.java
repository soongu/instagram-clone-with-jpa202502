package com.example.instagramclone.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileImageUpdateResponse {

    private String imageUrl;
    private String message;

    public static ProfileImageUpdateResponse of(String imageUrl, String message) {
        return ProfileImageUpdateResponse.builder()
                .imageUrl(imageUrl)
                .message(message)
                .build();
    }
}
