package com.example.instagramclone.service;

import com.example.instagramclone.domain.member.dto.request.LoginRequest;
import com.example.instagramclone.domain.member.dto.response.LoginResponse;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.exception.MemberException;
import com.example.instagramclone.exception.MemberErrorCode;
import com.example.instagramclone.repository.MemberRepository;
import com.example.instagramclone.domain.member.entity.RefreshToken;
import com.example.instagramclone.repository.RefreshTokenRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private MemberRepository memberRepository;
    
    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    // [TDD Step 1] 테스트 코드 작성 (로그인 실패 - 없는 회원)
    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 회원 (Username, Email, Phone 3가지 모두 하나로 통일됨)")
    void login_fail_user_not_found() {
        // given
        LoginRequest request = new LoginRequest("not_found_user", "password123!");

        // "not_found_user"는 정규식에 의해 username 조회로 분기되므로 findByUsername()을 stubbing
        given(memberRepository.findByUsername(request.username())).willReturn(Optional.empty());

        // when & then
        // 회원이 존재하지 않을 때 INVALID_CREDENTIALS 반환하는지 검증
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    // [TDD Step 3] 테스트 코드 작성 (로그인 실패 - 비밀번호 불일치)
    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치 시 없는 회원과 동일한 예외 반환")
    void login_fail_invalid_password() {
        // given
        LoginRequest request = new LoginRequest("test_user", "wrong_password");

        Member mockMember = Member.builder()
                .username("test_user")
                .password("encoded_correct_password")
                .build();
        ReflectionTestUtils.setField(mockMember, "id", 1L);

        given(memberRepository.findByUsername(eq(request.username())))
                .willReturn(Optional.of(mockMember));

        given(passwordEncoder.matches(eq(request.password()), eq(mockMember.getPassword())))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    // [TDD Step 5] 테스트 코드 작성 (로그인 성공)
    @Test
    @DisplayName("로그인 성공 - 토큰 페어 정상 발급")
    void login_success() {
        // given
        LoginRequest request = new LoginRequest("test_user", "correct_password");

        Member mockMember = Member.builder()
                .username("test_user")
                .password("encoded_correct_password")
                .build();
        ReflectionTestUtils.setField(mockMember, "id", 1L);

        given(memberRepository.findByUsername(eq(request.username())))
                .willReturn(Optional.of(mockMember));

        given(passwordEncoder.matches(eq(request.password()), eq(mockMember.getPassword())))
                .willReturn(true);

        given(refreshTokenRepository.findByMemberId(eq(1L))).willReturn(Optional.empty());
        given(jwtTokenProvider.createAccessToken(eq(1L), anyString())).willReturn("mock.access.token");
        given(jwtTokenProvider.createRefreshToken(eq(1L))).willReturn("mock.refresh.token");

        // when
        LoginResponse response = authService.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.tokens().accessToken()).isEqualTo("mock.access.token");
        assertThat(response.tokens().refreshToken()).isEqualTo("mock.refresh.token");
        assertThat(response.user().id()).isEqualTo(mockMember.getId());
        assertThat(response.user().username()).isEqualTo(mockMember.getUsername());

        // 행동 검증 (실무 필수)
        then(jwtTokenProvider).should().createAccessToken(eq(1L), anyString());
        then(jwtTokenProvider).should().createRefreshToken(eq(1L));
        then(refreshTokenRepository).should().save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("토큰 재발급 성공 - RTR 정책에 따라 DB 토큰 업데이트")
    void reissue_success() {
        // given
        String oldRefreshToken = "old.refresh.token";
        String newRefreshToken = "new.refresh.token";
        
        RefreshToken mockRefreshTokenEntity = RefreshToken.builder()
                .memberId(1L)
                .token(oldRefreshToken)
                .build();

        Member mockMember = Member.builder().username("test_user").build();
        ReflectionTestUtils.setField(mockMember, "id", 1L);

        given(jwtTokenProvider.validateToken(oldRefreshToken)).willReturn(true);
        given(refreshTokenRepository.findByToken(oldRefreshToken)).willReturn(Optional.of(mockRefreshTokenEntity));
        given(memberRepository.findById(1L)).willReturn(Optional.of(mockMember));
        given(jwtTokenProvider.createAccessToken(eq(1L), anyString())).willReturn("new.access.token");
        given(jwtTokenProvider.createRefreshToken(eq(1L))).willReturn(newRefreshToken);

        // when
        authService.reissue(oldRefreshToken);

        // then
        assertThat(mockRefreshTokenEntity.getToken()).isEqualTo(newRefreshToken); // DB 엔티티가 새 토큰으로 업데이트 되었는지 확인
        then(jwtTokenProvider).should().createAccessToken(eq(1L), anyString());
        then(jwtTokenProvider).should().createRefreshToken(eq(1L));
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 유효하지 않은 토큰")
    void reissue_fail_invalid_token() {
        // given
        String invalidToken = "invalid.token";
        given(jwtTokenProvider.validateToken(invalidToken)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.reissue(invalidToken))
                .isInstanceOf(MemberException.class)
                .hasMessage(MemberErrorCode.UNAUTHORIZED_ACCESS.getMessage());
    }

    @Test
    @DisplayName("로그아웃 성공 - DB에서 리프레시 토큰 삭제")
    void logout_success() {
        // given
        String refreshToken = "mock.refresh.token";
        RefreshToken mockRefreshTokenEntity = RefreshToken.builder()
                .memberId(1L)
                .token(refreshToken)
                .build();
        
        given(refreshTokenRepository.findByToken(refreshToken)).willReturn(Optional.of(mockRefreshTokenEntity));

        // when
        authService.logout(refreshToken);

        // then
        then(refreshTokenRepository).should().delete(mockRefreshTokenEntity);
    }
}
