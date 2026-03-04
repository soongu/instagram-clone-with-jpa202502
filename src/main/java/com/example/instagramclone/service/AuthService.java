package com.example.instagramclone.service;

import com.example.instagramclone.domain.member.dto.request.LoginRequest;
import com.example.instagramclone.domain.member.dto.response.AuthTokens;
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
    public AuthTokens login(LoginRequest request) {
        // [TDD Step 2] 원본 코드 작성 (로그인 실패 - 없는 회원)
        // 요구사항: username, email, phone 셋 중 하나를 선택해 로그인
        String loginId = request.username(); // 사용자 입력값 하나로 통일해서 검색 시도
        Member member = memberRepository.findByUsernameOrEmailOrPhone(loginId, loginId, loginId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_CREDENTIALS));

        // [TDD Step 4] 원본 코드 작성 (로그인 실패 - 비밀번호 불일치)
        // 요구사항: 비밀번호가 틀렸을 때도 동일한 보안 메시지를 반환해야 함 (보안 고려)
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_CREDENTIALS);
        }

        // [TDD Step 6] 원본 코드 작성 (로그인 성공 - 토큰 발급)
        String accessToken = jwtTokenProvider.createAccessToken(member.getId(), member.getRole().name());
        String refreshToken = jwtTokenProvider.createRefreshToken(member.getId());

        // Refresh Token DB 저장 로직 수행
        member.updateRefreshToken(refreshToken);

        return new AuthTokens(accessToken, refreshToken);
    }
}
