package com.example.instagramclone.service;

import com.example.instagramclone.core.exception.CommonErrorCode;
import com.example.instagramclone.core.exception.MemberErrorCode;
import com.example.instagramclone.core.exception.MemberException;
import com.example.instagramclone.domain.auth.api.SignUpRequest;
import com.example.instagramclone.domain.member.application.MemberService;
import com.example.instagramclone.domain.member.domain.Member;
import com.example.instagramclone.domain.member.domain.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("signUp() 로직 테스트")
    class SignUpTests {

        @Test
        @DisplayName("회원가입 실패 - 이메일 중복")
        void signUp_fail_duplicate_email() {
            SignUpRequest request = SignUpRequest.builder()
                    .username("new_user")
                    .password("password!23")
                    .emailOrPhone("test@test.com")
                    .name("New User")
                    .build();

            given(memberRepository.existsByEmail("test@test.com")).willReturn(true);

            assertThatThrownBy(() -> memberService.createMember(request))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_EMAIL.getMessage());

            then(passwordEncoder).shouldHaveNoInteractions();
            then(memberRepository).should(never()).save(any(Member.class));
        }

        @Test
        @DisplayName("회원가입 실패 - 전화번호 중복")
        void signUp_fail_duplicate_phone() {
            SignUpRequest request = SignUpRequest.builder()
                    .username("new_user")
                    .password("password!23")
                    .emailOrPhone("01012345678")
                    .name("New User")
                    .build();

            given(memberRepository.existsByPhone("01012345678")).willReturn(true);

            assertThatThrownBy(() -> memberService.createMember(request))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_PHONE.getMessage());

            then(passwordEncoder).shouldHaveNoInteractions();
            then(memberRepository).should(never()).save(any(Member.class));
        }

        @Test
        @DisplayName("회원가입 실패 - 유저네임 중복")
        void signUp_fail_duplicate_username() {
            SignUpRequest request = SignUpRequest.builder()
                    .username("existing_user")
                    .password("password!23")
                    .emailOrPhone("test@test.com")
                    .name("New User")
                    .build();

            given(memberRepository.existsByEmail("test@test.com")).willReturn(false);
            given(memberRepository.existsByUsername("existing_user")).willReturn(true);

            assertThatThrownBy(() -> memberService.createMember(request))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberErrorCode.DUPLICATE_USERNAME.getMessage());

            then(passwordEncoder).shouldHaveNoInteractions();
            then(memberRepository).should(never()).save(any(Member.class));
        }

        @Test
        @DisplayName("회원가입 성공 - (전화번호 가입)")
        void signUp_success_with_phone() {
            SignUpRequest request = SignUpRequest.builder()
                    .username("new_user")
                    .password("password!23")
                    .emailOrPhone("01012345678")
                    .name("New User")
                    .build();

            given(memberRepository.existsByPhone("01012345678")).willReturn(false);
            given(memberRepository.existsByUsername("new_user")).willReturn(false);
            given(passwordEncoder.encode(request.password())).willReturn("encoded!");

            memberService.createMember(request);

            then(memberRepository).should().save(any(Member.class));
        }

        @Test
        @DisplayName("회원가입 성공 - 이메일 및 유저네임 정상, 비밀번호 암호화 및 영속화 검증")
        void signUp_success() {
            SignUpRequest request = SignUpRequest.builder()
                    .username("new_user")
                    .password("password!23")
                    .emailOrPhone("test@test.com")
                    .name("New User")
                    .build();

            String encodedPassword = "encoded_password!23";

            given(memberRepository.existsByEmail("test@test.com")).willReturn(false);
            given(memberRepository.existsByUsername("new_user")).willReturn(false);
            given(passwordEncoder.encode(request.password())).willReturn(encodedPassword);

            memberService.createMember(request);

            then(passwordEncoder).should().encode(eq("password!23"));
            then(memberRepository).should().save(any(Member.class));
        }
    }

    @Nested
    @DisplayName("checkDuplicate() 로직 테스트")
    class CheckDuplicateTests {

        @Test
        @DisplayName("checkDuplicate - username")
        void checkDuplicate_username() {
            given(memberRepository.existsByUsername("testuser")).willReturn(true);
            boolean result = memberService.checkDuplicate("username", "testuser");
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("checkDuplicate - email")
        void checkDuplicate_email() {
            given(memberRepository.existsByEmail("test@test.com")).willReturn(false);
            boolean result = memberService.checkDuplicate("email", "test@test.com");
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("checkDuplicate - phone")
        void checkDuplicate_phone() {
            given(memberRepository.existsByPhone("01012345678")).willReturn(false);
            boolean result = memberService.checkDuplicate("phone", "01012345678");
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("checkDuplicate - 예외 케이스 (지원하지 않는 타입)")
        void checkDuplicate_invalid_type() {
            assertThatThrownBy(() -> memberService.checkDuplicate("invalid", "value"))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(CommonErrorCode.INVALID_INPUT_VALUE.getMessage());
        }
    }

    @Nested
    @DisplayName("getMemberById() 로직 테스트")
    class GetMemberByIdTests {

        @Test
        @DisplayName("getMemberById - 성공")
        void getMemberById_success() {
            Member mockMember = Member.builder().username("test").build();
            given(memberRepository.findById(1L)).willReturn(Optional.of(mockMember));

            Member result = memberService.findById(1L);
            assertThat(result.getUsername()).isEqualTo("test");
        }

        @Test
        @DisplayName("getMemberById - 실패 (회원 없음)")
        void getMemberById_not_found() {
            given(memberRepository.findById(999L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> memberService.findById(999L))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());
        }
    }
}
