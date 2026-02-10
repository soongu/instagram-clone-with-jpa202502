package com.example.instagramclone.service;

import com.example.instagramclone.domain.member.dto.request.SignUpRequest;
import com.example.instagramclone.domain.member.entity.Member;
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

    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        String emailOrPhone = signUpRequest.getEmailOrPhone();
        String email = null;
        String phone = null;

        // 이메일/전화번호 구분 및 중복체크
        if (emailOrPhone.contains("@")) {
            email = emailOrPhone;
            if (memberRepository.existsByEmail(email)) {
                throw new IllegalStateException("이미 존재하는 이메일입니다.");
            }
        } else {
            phone = emailOrPhone;
            if (memberRepository.existsByPhone(phone)) {
                throw new IllegalStateException("이미 존재하는 전화번호입니다.");
            }
        }

        // 사용자 이름 중복체크
        if (memberRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpRequest.getPassword());

        // Entity 변환
        Member member = Member.builder()
                .username(signUpRequest.getUsername())
                .password(encodedPassword)
                .email(email)
                .phone(phone)
                .name(signUpRequest.getName())
                .build();

        // 저장
        memberRepository.save(member);
    }

    // TODO: 2. 중복 체크 로직을 별도 메소드로 분리하세요
    
    // TODO: 3. 검증 로직을 구현하세요 (checkDuplicate)

}
