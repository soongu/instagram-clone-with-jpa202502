package com.example.instagramclone.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {

    private String message;
    private String username;

    // TODO: 1. 정적 팩토리 메소드 (of)를 만드세요
    
}
