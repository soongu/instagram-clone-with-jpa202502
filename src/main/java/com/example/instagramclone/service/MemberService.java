package com.example.instagramclone.service;

import com.example.instagramclone.domain.member.dto.request.LoginRequest;
import com.example.instagramclone.domain.member.dto.request.SignUpRequest;
import com.example.instagramclone.domain.member.dto.response.SessionUser;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.exception.CommonErrorCode;
import com.example.instagramclone.exception.MemberErrorCode;
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
                throw new MemberException(MemberErrorCode.DUPLICATE_EMAIL);
            }
        } else {
            phone = emailOrPhone;
            if (memberRepository.existsByPhone(phone)) {
                throw new MemberException(MemberErrorCode.DUPLICATE_PHONE);
            }
        }

        // 사용자 이름 중복체크
        if (memberRepository.existsByUsername(signUpRequest.username())) {
            throw new MemberException(MemberErrorCode.DUPLICATE_USERNAME);
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
            default -> throw new MemberException(CommonErrorCode.INVALID_INPUT_VALUE);
        };
    }

    // TODO: 4. 로그인 로직을 구현하세요 (username, email, phone 모두 지원)
    public SessionUser login(LoginRequest request) {
        String identifier = request.username(); // 클라이언트에서 username 필드 하나로 받음
        Member member;

        // 1. identifier 타입 분석 후 해당 값으로 Member 조회
        if (identifier.contains("@")) {
            member = memberRepository.findByEmail(identifier)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_CREDENTIALS));
        } else if (identifier.matches("^[0-9]+$")) { // 숫자만으로 이루어졌다면 전화번호로 간주
            member = memberRepository.findByPhone(identifier)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_CREDENTIALS));
        } else {
            member = memberRepository.findByUsername(identifier)
                    .orElseThrow(() -> new MemberException(MemberErrorCode.INVALID_CREDENTIALS));
        }

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new MemberException(MemberErrorCode.INVALID_CREDENTIALS);
        }

        // 3. 보안을 위해 Entity 대신 SessionUser DTO 변환 후 반환
        return SessionUser.from(member);
    }
}
