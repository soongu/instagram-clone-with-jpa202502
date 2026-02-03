package com.example.instagramclone.constant;

public class AuthConstants {
    
    public static final String ACCESS_TOKEN = "accessToken";
    public static final int COOKIE_MAX_AGE_DEFAULT = 60 * 60; // 1 hour
    public static final String LOGIN_SUCCESS_MESSAGE = "로그인에 성공했습니다.";
    public static final String LOGOUT_SUCCESS_MESSAGE = "로그아웃이 처리되었습니다.";
    public static final String SIGNUP_SUCCESS_MESSAGE = "회원가입이 완료되었습니다.";
    public static final String COOKIE_PATH_ROOT = "/";
    
    private AuthConstants() {
        // Prevent instantiation
    }
}
