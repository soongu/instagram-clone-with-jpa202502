package com.example.instagramclone.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {

        // 1. Request Header 에서 JWT 토큰 가로채기
        String token = resolveToken(request);

        // 2. 토큰 유효성 검사
        // TODO: [실습 2] 가로챈 토큰이 내용이 있고(null이 아님), 유효한 토큰인지(validateToken) 검사하세요.
        
        // 3. (Day 10) 유효한 토큰이면 Spring Security Context 에 인증 정보 심기
        // TODO: [실습 3] payload에서 id와 role을 추출하여, UsernamePasswordAuthenticationToken을 생성하고
        // SecurityContextHolder의 Context에 Authentication 객체로 저장하세요.

        // 4. 다음 필터로 요청 넘기기
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP Header 에서 토큰 값만 순수하게 추출하는 헬퍼 메서드
     */
    private String resolveToken(HttpServletRequest request) {
        // TODO: [실습 1] 요청의 "Authorization" 헤더를 가져온 뒤, 
        // 값이 "Bearer " 로 시작한다면 그 뒤의 순수 토큰 문자열만 잘라서 반환하세요.
        return null;
    }
}
