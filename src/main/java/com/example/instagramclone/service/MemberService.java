package com.example.instagramclone.service;

import com.example.instagramclone.domain.member.dto.request.SignUpRequest;
import com.example.instagramclone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // TODO: 1. 회원가입 로직을 구현하세요 (중복 체크 + 비밀번호 암호화 + DB 저장)
    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        // Implementation here
    }

    // TODO: 2. 중복 체크 로직을 별도 메소드로 분리하세요
    
    // TODO: 3. 검증 로직을 구현하세요 (checkDuplicate)

}
