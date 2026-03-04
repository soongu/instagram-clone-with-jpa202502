package com.example.instagramclone.controller;

import com.example.instagramclone.domain.member.dto.request.LoginRequest;
import com.example.instagramclone.domain.member.dto.request.SignUpRequest;
import com.example.instagramclone.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 테스트 데이터 롤백 보장
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @BeforeEach
    void setUp() {
        // given: 실제 비즈니스 로직(MemberService)을 통해 테스트용 유저 세팅 
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .username("integration_user")
                .emailOrPhone("inter@test.com")
                .password("password!23") 
                .name("Integration Test")
                .build();
        memberService.signUp(signUpRequest);
    }

    @Test
    @DisplayName("로그인 API 성공 흐름 검증 - SpringBootTest 통합 테스트")
    void login_api_success() throws Exception {
        // [TDD Step 5] 진짜 클라이언트가 호출하듯이 모든 레이어(Controller -> Service -> DB)를 관통하는 통합 테스트입니다.
        // given
        LoginRequest request = new LoginRequest("inter@test.com", "password!23"); // email 분기 테스트
        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.tokens.accessToken").exists())
                .andExpect(jsonPath("$.data.tokens.refreshToken").exists())
                .andExpect(jsonPath("$.data.user.username").value("integration_user"))
                .andExpect(jsonPath("$.data.user.name").value("Integration Test"));
    }

    @Test
    @DisplayName("로그인 API 실패 흐름 검증 - 존재하지 않는 회원")
    void login_api_fail_user_not_found() throws Exception {
        // [TDD Step 5.1] 존재하지 않는 회원(email)으로 로그인 시도
        // given
        LoginRequest request = new LoginRequest("notfound@test.com", "password!23");
        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized()) // INVALID_CREDENTIALS는 HTTP 401을 반환해야 합니다.
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.status").value(401))
                .andExpect(jsonPath("$.error.code").value("M005"))
                .andExpect(jsonPath("$.error.message").value("아이디 또는 비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.error.path").value("/api/auth/login"));
    }

    @Test
    @DisplayName("로그인 API 실패 흐름 검증 - 비밀번호 불일치")
    void login_api_fail_invalid_password() throws Exception {
        // [TDD Step 5.2] DB에 등록된 회원이지만 비밀번호가 틀린 경우
        // given
        LoginRequest request = new LoginRequest("inter@test.com", "wrongpassword!23");
        String content = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isUnauthorized()) // 보안상 존재하지 않는 회원과 동일하게 401 에러를 내려줍니다.
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.status").value(401))
                .andExpect(jsonPath("$.error.code").value("M005"))
                .andExpect(jsonPath("$.error.message").value("아이디 또는 비밀번호가 일치하지 않습니다."))
                .andExpect(jsonPath("$.error.path").value("/api/auth/login"));
    }
}
