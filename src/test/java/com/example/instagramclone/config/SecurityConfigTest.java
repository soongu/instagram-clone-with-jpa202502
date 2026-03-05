package com.example.instagramclone.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("인증되지 않은 사용자가 public API(로그인, 회원가입 등)에 접근하면 200 OK 또는 관련 상태 코드를 반환한다")
    void unauthenticatedUser_canAccess_publicApis() throws Exception {
        // 회원가입 API (Validation 에러로 400이 뜰 수 있으므로 인증(403) 방어벽은 뚫렸는지 확인)
        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content("{}"))
                .andExpect(status().isBadRequest()); // 403이 아니면 인가 처리는 통과한 것

        // 중복 검사 API
        mockMvc.perform(get("/api/auth/check-duplicate")
                .param("type", "username")
                .param("value", "testuser"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("인증되지 않은 사용자가 보호된 API(예: 로그아웃, 피드 조회 등)에 접근하면 403 Forbidden 을 반환한다 (우리가 아직 401 처리를 안 했기 때문!)")
    void unauthenticatedUser_cannotAccess_protectedApis() throws Exception {
        // [실습 포인트] 
        // 분명 인증되지 않은 요청이므로 401 Unauthorized가 와야 할 것 같지만,
        // 스프링 시큐리티는 기본적으로 우리가 AuthenticationEntryPoint를 설정하지 않으면 403 Forbidden을 내려보냅니다!
        // 이 현상을 직접 눈으로 확인한 뒤, "과제 2번: AuthenticationEntryPoint 구현"을 도입하는 완벽한 빌드업입니다.
        
        // 로그아웃 (permitAll에 없으므로 인증 필요 -> 토큰 없으므로 403 발생)
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isForbidden());

        // 일반 보호된 API (존재하지 않는 API라도 시큐리티 단에서 먼저 403을 내려줘야 함)
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isForbidden());
    }
}
