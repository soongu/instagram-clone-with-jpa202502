package com.example.instagramclone.domain.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DuplicateCheckResponse {

    private boolean isAvailable;
    private String message;

    // TODO: 1. 정적 팩토리 메소드 (available, unavailable)를 만드세요
    
}
