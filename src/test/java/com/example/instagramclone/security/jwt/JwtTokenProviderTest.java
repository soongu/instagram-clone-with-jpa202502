package com.example.instagramclone.security.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

//@SpringBootTest // 단위 테스트의 빠른 속도를 체감하기 위해 스프링 컨텍스트를 띄우지 않습니다.
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();

        // Hint: ReflectionTestUtils를 쓰거나, 파라미터가 있는 생성자/Setter를 이용해
        // application.yml 의 값을 강제로 주입(Mocking이 아님)해 주어야 합니다.
        String plainSecret = "ThisIsASuperSecretKeyForJwtTokenGeneration";
        String base64Secret = Base64.getEncoder().encodeToString(plainSecret.getBytes());

        ReflectionTestUtils.setField(jwtTokenProvider, "secretKeyString", base64Secret);
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidityInMilliseconds", 3600000L); // 1시간
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshTokenValidityInMilliseconds", 864000000L); // 10일

        // 빈 초기화 시점에 작동하는 로직(@PostConstruct)을 직접 호출하여 SecretKey 객체를 생성합니다.
        jwtTokenProvider.init();
    }

    @Test
    @DisplayName("Access Token 발급 및 유저 PK 추출 검증")
    void createAndParseAccessToken() {
        // given: 유저 ID(PK) 1L과 ROLE_USER 권한 설정
        Long memberId = 1L;
        String role = "ROLE_USER";

        // when: provider.createAccessToken() 호출
        String accessToken = jwtTokenProvider.createAccessToken(memberId, role);

        // then: provider.getMemberId(token)로 파싱한 값이 1L과 일치하는지 assertThat 으로 검증
        assertThat(accessToken).isNotBlank();
        Long parsedMemberId = jwtTokenProvider.getMemberId(accessToken);
        assertThat(parsedMemberId).isEqualTo(memberId);

        // 토큰 자체가 유효한지 검증
        assertThat(jwtTokenProvider.validateToken(accessToken)).isTrue();
    }

    @Test
    @DisplayName("Refresh Token 발급 및 유저 PK 추출 검증")
    void createAndParseRefreshToken() {
        // given: 유저 ID(PK) 2L
        Long memberId = 2L;

        // when: provider.createRefreshToken() 호출
        String refreshToken = jwtTokenProvider.createRefreshToken(memberId);

        // then:
        assertThat(refreshToken).isNotBlank();
        Long parsedMemberId = jwtTokenProvider.getMemberId(refreshToken);
        assertThat(parsedMemberId).isEqualTo(memberId);

        assertThat(jwtTokenProvider.validateToken(refreshToken)).isTrue();
    }

    @Test
    @DisplayName("만료된 토큰 검증 시 validateToken 은 false 를 반환해야 한다")
    void validateExpiredToken() throws InterruptedException {
        // given: 유효기간이 1ms인 매우 짧은 토큰 생성 설정을 강제 주입
        ReflectionTestUtils.setField(jwtTokenProvider, "accessTokenValidityInMilliseconds", 1L);
        String expiredToken = jwtTokenProvider.createAccessToken(3L, "ROLE_USER");

        // when: 토큰이 만료되도록 충분한 시간(10ms) 대기
        Thread.sleep(10);

        // then: validateToken 메서드는 예외를 던지지 않고 내부적으로 catch 하여 false를 반환해야 합니다.
        boolean isValid = jwtTokenProvider.validateToken(expiredToken);
        assertThat(isValid).isFalse();
    }
}
