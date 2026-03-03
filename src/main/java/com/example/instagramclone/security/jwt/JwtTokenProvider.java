package com.example.instagramclone.security.jwt;

import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    // TODO: [실습 1] application.yml에 설정해둔 jwt.secret 값을 @Value로 주입받으세요.
    private final String secretKey = "super-secret-key-for-jwt-signature-super-secret-key-for-jwt-signature";

    // Access Token 만료 시간 (예: 1시간)
    private final long accessTokenValidityInMilliseconds = 1000L * 60 * 60; 

    /**
     * 회원(User)의 정보를 기반으로 Access Token을 생성합니다.
     * @param email 회원의 이메일 (또는 ID)
     * @param role 회원의 권한 (ROLE_USER 등)
     * @return 발급된 JWT 토큰 문자열
     */
    public String createToken(String email, String role) {
        
        // TODO: [실습 2] 토큰에 담을 정보(Payload - claims)를 세팅하세요. (예: email, role)
        // 주의: 비밀번호 같은 민감정보는 절대 넣지 마세요! (jwt.io 시연 예정)
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        // TODO: [실습 3] Header, Payload, Signature 를 조합하여 토큰을 생성(서명)하는 로직을 작성하세요.
        // HmacSHA256 알고리즘과 주입받은 secretKey를 사용합니다.
        
        return "temporary-token-string"; // 임시 반환값
    }

    /**
     * 토큰에서 유저 이메일(Subject)을 추출합니다.
     */
    public String getEmail(String token) {
        // TODO: [실습 4] 전달받은 토큰의 서명을 확인하고, Payload에서 이메일을 추출해 반환하세요.
        return null;
    }

    /**
     * 토큰의 유효성 및 만료 기간을 검사합니다.
     */
    public boolean validateToken(String token) {
        // TODO: [실습 5] 토큰 파싱 시 발생할 수 있는 예외(만료, 위조, 지원되지 않는 포맷 등)를 잡아서 
        // 유효하면 true, 아니면 false를 반환하도록 작성하세요.
        return false;
    }
}
