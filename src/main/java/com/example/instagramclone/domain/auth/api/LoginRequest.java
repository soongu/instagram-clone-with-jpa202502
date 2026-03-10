package com.example.instagramclone.domain.member.api;


import com.example.instagramclone.core.aop.annotation.Masking;

public record LoginRequest(
    String username,

    @Masking
    String password
) {
}
