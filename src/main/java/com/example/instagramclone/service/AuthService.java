package com.example.instagramclone.service;

import com.example.instagramclone.domain.member.dto.request.LoginRequest;
import com.example.instagramclone.domain.member.dto.response.AuthTokens;
import com.example.instagramclone.domain.member.dto.response.LoginResponse;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.exception.MemberErrorCode;
import com.example.instagramclone.exception.MemberException;
import com.example.instagramclone.repository.MemberRepository;
import com.example.instagramclone.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // [Refactor] 정규식을 이용한 단일 쿼리 분기 처리 (성능 및 인덱스 최적화)
        String loginId = request.username();
        Member member;

        if (loginId.contains("@")) {
            // 이메일 형식
            member = memberRepository.findByEmail(loginId)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_CREDENTIALS));
        } else if (loginId.matches("^[0-9\\-]+$")) {
            // 전화번호 형식 (숫자와 하이픈만 포함)
            member = memberRepository.findByPhone(loginId)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_CREDENTIALS));
        } else {
            // 그 외: 유저네임
            member = memberRepository.findByUsername(loginId)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_CREDENTIALS));
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_CREDENTIALS);
        }

        // 토큰 발급
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());
        member.updateRefreshToken(refreshToken);

        // [Refactor] 프론트엔드 요구사항에 맞춘 풍부한 응답 DTO 반환 (정적 팩토리 메서드 활용)
        AuthTokens tokens = new AuthTokens(accessToken, refreshToken);
        return LoginResponse.of(tokens, member);
    }
}
