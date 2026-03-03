package com.example.instagramclone.domain.member.dto.response;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {}
