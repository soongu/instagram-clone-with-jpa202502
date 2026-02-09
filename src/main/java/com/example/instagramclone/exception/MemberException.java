package com.example.instagramclone.exception;

import lombok.Getter;

@Getter
public class MemberException extends RuntimeException {

    // TODO: 1. ErrorCode를 필드로 가지는 커스텀 예외를 완성하세요
    
    public MemberException(String message) {
        super(message);
    }
}