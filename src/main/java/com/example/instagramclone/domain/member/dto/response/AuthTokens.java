package com.example.instagramclone.domain.member.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record AuthTokens(
        String accessToken,
        @JsonIgnore
        String refreshToken
) {}
