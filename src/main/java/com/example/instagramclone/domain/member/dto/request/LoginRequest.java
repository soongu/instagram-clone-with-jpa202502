package com.example.instagramclone.domain.member.dto.request;

import com.example.instagramclone.aop.annotation.Masking;

public record LoginRequest(
    String username,
    
    @Masking
    String password
) {
}
