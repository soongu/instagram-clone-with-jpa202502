package com.example.instagramclone.security.jwt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest // 단위 테스트의 빠른 속도를 체감하기 위해 스프링 컨텍스트를 띄우지 않습니다.
class JwtTokenProviderTest {

    // TODO: [실습 1] JwtTokenProvider 인스턴스를 직접 생성하고(수동 의존성 주입), 토큰 발급 테스트를 작성하세요.
    // Hint: ReflectionTestUtils를 쓰거나, 파라미터가 있는 생성자/Setter를 이용해 
    // application.yml 의 값을 강제로 주입(Mocking이 아님)해 주어야 합니다.
    
    @Test
    @DisplayName("Access Token 발급 및 유저 PK 추출 검증")
    void createAndParseAccessToken() {
        // given: 유저 ID(PK) 1L과 ROLE_USER 권한 설정
        
        // when: provider.createAccessToken() 호출
        
        // then: provider.getMemberId(token)로 파싱한 값이 1L과 일치하는지 assertThat 으로 검증
    }
}
