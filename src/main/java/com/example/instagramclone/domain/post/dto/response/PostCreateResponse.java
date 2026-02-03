package com.example.instagramclone.domain.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostCreateResponse {

    private Long id;
    private String message;

    public static PostCreateResponse of(Long id) {
        return PostCreateResponse.builder()
                .id(id)
                .message("save success")
                .build();
    }
}
