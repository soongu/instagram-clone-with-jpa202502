package com.example.instagramclone.service;

import com.example.instagramclone.constant.AuthConstants;
import com.example.instagramclone.domain.member.dto.request.LoginRequest;
import com.example.instagramclone.domain.member.dto.request.SignUpRequest;
import com.example.instagramclone.domain.member.dto.response.DuplicateCheckResponse;
import com.example.instagramclone.domain.member.dto.response.LoginResponse;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.exception.ErrorCode;
import com.example.instagramclone.exception.MemberException;
import com.example.instagramclone.jwt.JwtTokenProvider;
import com.example.instagramclone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@Transactional(readOnly = true) // 트랜잭션 처리
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    private final MemberRepository memberRepository;

    // 회원가입 중간처리
    @Transactional
    public void signUp(SignUpRequest signUpRequest) {
        
        // Race condition 및 중복 방지를 위한 검증 로직 추출
        validateDuplicateMember(signUpRequest);

        // 순수 비밀번호를 꺼내서 암호화
        String rawPassword = signUpRequest.getPassword();
        // 암호화된 패스워드
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 회원정보를 엔터티로 변환
        Member newMember = signUpRequest.toEntity();
        // 패스워드를 인코딩패스워드로 교체
        newMember.setPassword(encodedPassword);

        // DB에 전송
        memberRepository.save(newMember);
    }

    private void validateDuplicateMember(SignUpRequest signUpRequest) {
        /*
            Race condition 방지

            사용자가 중복체크 후 회원가입 버튼을 누르기 전까지의 시간동안
            다른 사용자가 같은 값으로 가입할 수 있음
            이를 최종 회원가입에서 한번 더 검사해서 방지
         */
        String emailOrPhone = signUpRequest.getEmailOrPhone();
        if (emailOrPhone.contains("@")) {
            memberRepository.findByEmail(emailOrPhone)
                    .ifPresent(m -> {
                        throw new MemberException(ErrorCode.DUPLICATE_EMAIL);
                    });
        } else {
            memberRepository.findByPhone(emailOrPhone)
                    .ifPresent(m -> {
                        throw new MemberException(ErrorCode.DUPLICATE_PHONE);
                    });
        }

        memberRepository.findByUsername(signUpRequest.getUsername())
                .ifPresent(m -> {
                    throw new MemberException(ErrorCode.DUPLICATE_USERNAME);
                });
    }

    /**
     * 중복 검사 통합 처리 (이메일, 전화번호, 유저네임)
     *
     * @param type - 검사할 값의 타입 (email, phone, username)
     * @param value - 실제로 중복을 검사할 값
     */
    public DuplicateCheckResponse checkDuplicate(String type, String value) {
        switch (type) {
            case "email":
                return convertResponse(memberRepository.findByEmail(value), "이미 사용 중인 이메일입니다.");
            case "phone":
                return convertResponse(memberRepository.findByPhone(value), "이미 사용 중인 전화번호입니다.");
            case "username":
                return convertResponse(memberRepository.findByUsername(value), "이미 사용 중인 사용자 이름입니다.");
            default:
                throw new MemberException(ErrorCode.INVALID_SIGNUP_DATA);
        }
    }

    private DuplicateCheckResponse convertResponse(Optional<Member> memberToCheck, String errorMessage) {
        return memberToCheck
                .map(m -> DuplicateCheckResponse.unavailable(errorMessage))
                .orElse(DuplicateCheckResponse.available());
    }

    // 로그인 처리 (인증 처리)
    /*
        1. 클라이언트가 전달한 계정명(이메일, 전화번호, 유저네임)과
        패스워드를 받아야 함
        2. 계정명을 데이터베이스에 조회해서 존재하는지 유무 확인
        3. 존재한다면 회원정보를 데이터베이스에서 받아와서 비밀번호를 꺼내옴
        4. 패스워드 일치를 검사
     */
    public LoginResponse authenticate(LoginRequest loginRequest) {

        // 로그인 시도하는 계정명 (이메일, 전화번호, 사용자이름)
        String username = loginRequest.getUsername();

        // 3번의 쿼리 가능성을 1번의 쿼리로 통합
        Member foundMember = memberRepository.findByUsernameOrEmailOrPhone(username, username, username)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 사용자가 입력한 패스워드와 DB에 저장된 패스워드를 추출
        String inputPassword = loginRequest.getPassword();
        String storedPassword = foundMember.getPassword();

        // 비번이 일치하지 않으면 예외 발생
        // 암호화된 비번을 디코딩해서 비교해야 함
        if (!passwordEncoder.matches(inputPassword, storedPassword)) {
            throw new MemberException(ErrorCode.INVALID_PASSWORD);
        }

        log.info("foundmember: {}", foundMember);
        // 로그인이 성공했을 때 JSON 생성 (액세스토큰을 포함)
        return LoginResponse.of(
                AuthConstants.LOGIN_SUCCESS_MESSAGE,
                foundMember.getUsername(),
                jwtTokenProvider.createAccessToken(foundMember.getUsername()),
                foundMember.getProfileImageUrl() == null ? "null" : foundMember.getProfileImageUrl()
        );
    }
}
