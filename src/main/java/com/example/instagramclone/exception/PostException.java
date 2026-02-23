package com.example.instagramclone.exception;

import lombok.Getter;

@Getter
public class PostException extends BusinessException {

    public PostException(ErrorCode errorCode) {
        super(errorCode);
    }
}
