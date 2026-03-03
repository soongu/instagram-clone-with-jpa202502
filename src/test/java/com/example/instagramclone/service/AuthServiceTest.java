package com.example.instagramclone.service;

import com.example.instagramclone.domain.member.dto.request.LoginRequest;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.exception.MemberException;
import com.example.instagramclone.repository.MemberRepository;
import com.example.instagramclone.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 회원")
    void login_fail_user_not_found() {
        // TODO: [실습 2-1] Mockito를 활용하여 회원이 존재하지 않을 때 예외가 발생하는지 검증하세요.
        // 1. given: LoginRequest 객체 생성 (더미 데이터)
        LoginRequest request = new LoginRequest("not_found_user", "password123!");
        
        // 2. given: memberRepository.findBy...() 호출 시 Optional.empty() 반환되도록 stubbing
        // Note: 로그인 시 username으로 회원을 조회한다고 가정
        given(memberRepository.findByUsername(anyString())).willReturn(Optional.empty());

        // 3. when & then: authService.login() 호출 시 MemberException 발생하는지 검증 (assertThrows 사용)
        assertThrows(MemberException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_invalid_password() {
        // TODO: [실습 2-2] 비밀번호가 틀렸을 때 예외가 발생하는지 검증하세요.
        // 1. given: 가짜 Member 엔티티 생성 후 repository 동작 stubbing
        LoginRequest request = new LoginRequest("test_user", "wrong_password");
        
        Member mockMember = Member.builder()
                .username("test_user")
                .password("encoded_correct_password")
                .build();
        ReflectionTestUtils.setField(mockMember, "id", 1L);
                
        // Note: 현재 UserService 등에 의해 username 으로 회원을 조회한다고 가정
        given(memberRepository.findByUsername(anyString())).willReturn(Optional.of(mockMember));
        
        // 2. given: passwordEncoder.matches() 호출 시 false 반환되도록 stubbing
        given(passwordEncoder.matches("wrong_password", "encoded_correct_password")).willReturn(false);

        // 3. when & then: 예외 발생 검증
        assertThrows(MemberException.class, () -> authService.login(request));
    }
    
    @Test
    @DisplayName("로그인 성공 - 토큰 페어 정상 발급")
    void login_success() {
        // TODO: [실습 3-1] 나중에 AuthService.login() 실제 비즈니스 로직을 완성한 뒤에 성공 케이스 테스트도 작성해보세요.
    }
}
