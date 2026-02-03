package com.example.instagramclone.util;

import com.example.instagramclone.constant.AuthConstants;
import jakarta.servlet.http.Cookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtils {

    /**
     * 쿠키 생성
     *
     * @param name - 쿠키 이름
     * @param value - 쿠키 값
     * @param maxAge - 쿠키 수명 (초 단위)
     * @return 생성된 쿠키
     */
    public Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath(AuthConstants.COOKIE_PATH_ROOT);
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(true);
        return cookie;
    }

    /**
     * 쿠키 삭제 (수명을 0으로 설정)
     *
     * @param name - 삭제할 쿠키 이름
     * @return 수명이 0인 쿠키
     */
    public Cookie deleteCookie(String name) {
        return createCookie(name, null, 0);
    }
}
