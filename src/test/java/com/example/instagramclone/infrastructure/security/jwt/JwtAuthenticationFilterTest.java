package com.example.instagramclone.infrastructure.security.jwt;

import com.example.instagramclone.core.constant.AuthConstants;
import com.example.instagramclone.infrastructure.security.jwt.JwtAuthenticationFilter;
import com.example.instagramclone.infrastructure.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.instagramclone.domain.member.domain.MemberRole;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        // 스프링이 제공하는 가짜(Mock) HttpServlet 객체 초기화
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // 각 테스트가 시작될 때마다 SecurityContext를 깨끗하게 비워줍니다.
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        // 테스트 종료 후에도 Context를 비워 다른 테스트에 영향을 주지 않도록 합니다.
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("유효한 JWT 토큰이 주어지면, SecurityContext에 인증 정보가 저장된다.")
    void doFilterInternal_validToken() throws ServletException, IOException {
        // given (준비)
        String validToken = "valid.jwt.token";
        Long expectedMemberId = 1L;
        String expectedRole = MemberRole.USER.getKey();

        // 요청 헤더에 Authorization 세팅
        request.addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_PREFIX + validToken);

        // JwtTokenProvider가 호출되었을 때 모의(Mock) 응답 설정
        given(jwtTokenProvider.validateToken(validToken)).willReturn(true);
        given(jwtTokenProvider.getMemberId(validToken)).willReturn(expectedMemberId);
        given(jwtTokenProvider.getRole(validToken)).willReturn(expectedRole);

        // when (실행)
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then (검증)
        // 1. SecurityContextHolder에 어떤 인증 객체가 들어갔는지 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        assertThat(auth).isNotNull(); // 인증 객체가 만들어졌어야 함
        assertThat(auth.getPrincipal()).isEqualTo(expectedMemberId); // 주체(Principal)는 ID 이어야 함
        assertThat(auth.getAuthorities())
                .extracting("authority")
                .containsExactly(expectedRole); // 권한이 정상적으로 들어갔어야 함

        // 2. 반드시 다음 필터로 넘겨주었는지 검증
        then(filterChain).should().doFilter(request, response);
    }

    @Test
    @DisplayName("토큰이 아예 없는 경우, 인증 정보 없이 다음 필터로 넘어간다.")
    void doFilterInternal_noToken() throws ServletException, IOException {
        // given (준비)
        // Header를 아예 세팅하지 않음

        // when (실행)
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then (검증)
        // 인증 정보가 담기지 않았어야 함
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull();

        // 그럼에도 다음 필터로는 정상적으로 넘어가야 함 (인가 필터에서 401로 튕겨내도록)
        then(filterChain).should().doFilter(request, response);
    }

    @Test
    @DisplayName("Bearer 접두사가 없는 잘못된 형식의 헤더인 경우, 토큰 파싱을 무시하고 그냥 넘어간다.")
    void doFilterInternal_invalidPrefixPrefix() throws ServletException, IOException {
        // given (준비)
        String invalidHeaderValue = "Basic some.base64.encoded.string="; // Bearer 가 아님!
        request.addHeader(AuthConstants.AUTHORIZATION_HEADER, invalidHeaderValue);

        // when (실행)
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then (검증)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull(); // 파싱을 안했으니 인증 도장이 없어야 함

        then(filterChain).should().doFilter(request, response);
    }

    @Test
    @DisplayName("토큰의 형태는 갖췄으나 만료되거나 서명이 틀린 유효하지 않은 토큰인 경우, 인증 없이 넘어간다.")
    void doFilterInternal_invalidToken() throws ServletException, IOException {
        // given (준비)
        String invalidToken = "invalid.jwt.token";
        request.addHeader(AuthConstants.AUTHORIZATION_HEADER, AuthConstants.BEARER_PREFIX + invalidToken);

        // JwtTokenProvider가 유효하지 않은 토큰이라고 false 를 반환하도록 설정
        given(jwtTokenProvider.validateToken(invalidToken)).willReturn(false);

        // when (실행)
        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        // then (검증)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNull(); // validateToken을 통과하지 못해 도장을 못 받았어야 함

        then(filterChain).should().doFilter(request, response);
    }
}
