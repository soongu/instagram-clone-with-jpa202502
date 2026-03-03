package com.example.instagramclone.controller;

import com.example.instagramclone.controller.rest.AuthController;
import com.example.instagramclone.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService; // 가짜 서비스 객체 주입

    @Test
    @DisplayName("로그인 API 성공 흐름 검증 - MockMvc 활용")
    void login_api_success() throws Exception {
        // TODO: [실습 4] WebMvcTest와 MockMvc를 활용하여 로그인 API의 HTTP 응답 구조를 검증하세요.
        // 1. given: LoginRequest JSON 문자열 생성 (ObjectMapper 활용)
        // 2. given: authService.login() 호출 시 AuthTokens 더미 객체를 반환하도록 stubbing
        // 3. when: mockMvc.perform(post("/api/auth/login") ... ) 실행
        // 4. then: andExpect(status().isOk()) 및 jsonPath("$.data.accessToken").exists() 확인
    }
}
