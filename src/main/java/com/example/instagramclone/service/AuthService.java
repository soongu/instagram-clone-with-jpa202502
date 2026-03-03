package com.example.instagramclone.service;

import com.example.instagramclone.domain.member.dto.request.LoginRequest;
import com.example.instagramclone.domain.member.dto.response.AuthTokens;
import com.example.instagramclone.repository.MemberRepository;
import com.example.instagramclone.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthTokens login(LoginRequest request) {
        // TODO: [실습 3] TDD를 통해 로그인 비즈니스 로직을 완성하세요.
        // Hint 1. request.username() (이메일, 전화번호, 닉네임) 으로 Member 조회
        // Hint 2. 검색 실패 시 MemberException(INVALID_CREDENTIALS) 발생시켜야 함
        // Hint 3. 비밀번호 검증 (passwordEncoder.matches) 실패 시 예외 발생시켜야 함
        // Hint 4. 성공 시 jwtTokenProvider를 통해 Access, Refresh Token 발급 후 AuthTokens 반환
        return null; // 처음엔 테스트 코드를 실패(Red)하게 만들기 위해 일부러 null 반환
    }
}
