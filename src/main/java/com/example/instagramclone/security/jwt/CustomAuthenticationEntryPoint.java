package com.example.instagramclone.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // TODO: [과제 2] Filter 단에서 발생한 인증 예외(토큰 만료 등)를 가로채어
        // GlobalExceptionHandler 처럼 ApiResponse 형태의 JSON으로 클라이언트에게 응답하도록 구현하세요.
        // 응답 상태 코드는 401 Unauthorized 입니다.
    }
}
