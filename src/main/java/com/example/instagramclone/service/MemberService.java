package com.example.instagramclone.service;

import com.example.instagramclone.domain.member.dto.request.SignUpRequest;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.exception.ErrorCode;
import com.example.instagramclone.exception.MemberException;
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
        String emailOrPhone = signUpRequest.emailOrPhone();
        String email = null;
        String phone = null;

        // 이메일/전화번호 구분 및 중복체크
        if (emailOrPhone.contains("@")) {
            email = emailOrPhone;
            if (memberRepository.existsByEmail(email)) {
                throw new MemberException(ErrorCode.DUPLICATE_EMAIL);
            }
        } else {
            phone = emailOrPhone;
            if (memberRepository.existsByPhone(phone)) {
                throw new MemberException(ErrorCode.DUPLICATE_PHONE);
            }
        }

        // 사용자 이름 중복체크
        if (memberRepository.existsByUsername(signUpRequest.username())) {
            throw new MemberException(ErrorCode.DUPLICATE_USERNAME);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signUpRequest.password());

        // Entity 변환
        Member member = Member.builder()
                .username(signUpRequest.username())
                .password(encodedPassword)
                .email(email)
                .phone(phone)
                .name(signUpRequest.name())
                .build();

        // 저장
        memberRepository.save(member);
    }

    // TODO: 2. 중복 체크 로직을 별도 메소드로 분리하세요
    

    // TODO: 3. 검증 로직을 구현하세요 (checkDuplicate)
    public boolean checkDuplicate(String type, String value) {
        return switch (type) {
            case "username" -> !memberRepository.existsByUsername(value);
            case "email" -> !memberRepository.existsByEmail(value);
            case "phone" -> !memberRepository.existsByPhone(value);
            default -> throw new MemberException(ErrorCode.INVALID_INPUT_VALUE);
        };
    }

    
    

}
