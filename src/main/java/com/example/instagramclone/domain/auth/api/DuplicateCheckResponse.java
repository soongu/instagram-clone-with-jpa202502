package com.example.instagramclone.domain.member.api;

import lombok.Builder;

@Builder
public record DuplicateCheckResponse(
        boolean available,
        String message
) {
    public static DuplicateCheckResponse available(String message) {
        return new DuplicateCheckResponse(true, message);
    }

    public static DuplicateCheckResponse unavailable(String message) {
        return new DuplicateCheckResponse(false, message);
    }
}
