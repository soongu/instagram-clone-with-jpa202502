package com.example.instagramclone.controller.rest;

import com.example.instagramclone.constant.AuthConstants;
import com.example.instagramclone.domain.common.dto.ApiResponse;
import com.example.instagramclone.domain.member.dto.request.LoginRequest;
import com.example.instagramclone.domain.member.dto.request.SignUpRequest;
import com.example.instagramclone.domain.member.dto.response.DuplicateCheckResponse;
import com.example.instagramclone.domain.member.dto.response.LoginResponse;
import com.example.instagramclone.domain.member.dto.response.AuthTokens;
import com.example.instagramclone.domain.member.dto.response.SignUpResponse;
import com.example.instagramclone.security.jwt.JwtTokenProvider;
import com.example.instagramclone.service.AuthService;
import com.example.instagramclone.service.MemberService;
import com.example.instagramclone.exception.MemberException;
import com.example.instagramclone.exception.MemberErrorCode;
import com.example.instagramclone.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtils cookieUtils;

    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        memberService.signUp(signUpRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(SignUpResponse.of(signUpRequest.username(), AuthConstants.SIGNUP_SUCCESS_MESSAGE)));
    }

    @GetMapping("/check-duplicate")
    public ResponseEntity<ApiResponse<DuplicateCheckResponse>> checkDuplicate(@RequestParam String type, @RequestParam String value) {
        boolean isAvailable = memberService.checkDuplicate(type, value);
        String message = isAvailable ? "사용 가능한 " + type + "입니다." : "이미 사용 중인 " + type + "입니다.";

        DuplicateCheckResponse response = isAvailable ?
                DuplicateCheckResponse.available(message) :
                DuplicateCheckResponse.unavailable(message);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(loginRequest);

        // HttpOnly 옵션과 Secure 옵션을 설정한 쿠키(Cookie)로 생성하여 HttpServletResponse 객체에 추가하세요.
        // 쿠키 이름은 "refresh_token" 으로 설정합니다.
        
        // 주의: AuthTokens DTO에서 @JsonIgnore 를 설정하여 클라이언트의 JSON 바디에는 노출되지 않지만,
        // 서버에서는 loginResponse.tokens().refreshToken() 으로 안전하게 꺼내 쓸 수 있습니다.
        Cookie cookie = cookieUtils.createCookie(
                AuthConstants.REFRESH_TOKEN,
                loginResponse.tokens().refreshToken(),
                jwtTokenProvider.getRefreshTokenValidityInSeconds()
        );
        response.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<AuthTokens>> reissue(
            @CookieValue(value = AuthConstants.REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response) {
        
        // 추출한 토큰을 authService.reissue() 에 넘겨주세요.
        // 재발급 받은 새로운 RefreshToken 역시 HttpOnly 쿠키로 응답에 세팅해야 합니다.
        if (refreshToken == null) {
            // 커스텀 인증 에러 처리 (로그인 만료)
            throw new MemberException(MemberErrorCode.UNAUTHORIZED_ACCESS);
        }

        AuthTokens tokens = authService.reissue(refreshToken);
        
        // 새로 발급된 리프레시 토큰으로 쿠키 덮어쓰기 (RTR 방식)
        Cookie cookie = cookieUtils.createCookie(
                AuthConstants.REFRESH_TOKEN,
                tokens.refreshToken(),
                jwtTokenProvider.getRefreshTokenValidityInSeconds()
        );
        response.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(
            @CookieValue(value = AuthConstants.REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response) {
        
        // 1. DB에서 Refresh Token 삭제
        if (refreshToken != null) {
            authService.logout(refreshToken);
        }

        // 2. 브라우저의 Refresh Token 쿠키 무효화 
        Cookie cookie = cookieUtils.deleteCookie(AuthConstants.REFRESH_TOKEN);
        response.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.success(AuthConstants.LOGOUT_SUCCESS_MESSAGE));
    }
}
