package com.example.instagramclone.controller.rest;

import com.example.instagramclone.constant.AuthConstants;
import com.example.instagramclone.domain.common.dto.ApiResponse;
import com.example.instagramclone.domain.member.dto.request.LoginRequest;
import com.example.instagramclone.domain.member.dto.request.SignUpRequest;
import com.example.instagramclone.domain.member.dto.response.DuplicateCheckResponse;
import com.example.instagramclone.domain.member.dto.response.LoginResponse;
import com.example.instagramclone.domain.member.dto.response.AuthTokens;
import com.example.instagramclone.domain.member.dto.response.SignUpResponse;
import com.example.instagramclone.service.AuthService;
import com.example.instagramclone.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final AuthService authService;

    
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

        // TODO: [실습 5-4] AuthService에서 반환받은 loginResponse 안에 있는 RefreshToken을
        // HttpOnly 옵션과 Secure 옵션을 설정한 쿠키(Cookie)로 생성하여 HttpServletResponse 객체에 추가하세요.
        // 쿠키 이름은 "refresh_token" 으로 설정합니다.

        return ResponseEntity.ok(ApiResponse.success(loginResponse));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<AuthTokens>> reissue() {
        // TODO: [실습 6-1] @CookieValue 어노테이션을 사용하여 요청 쿠키에서 "refresh_token" 값을 받아오세요.
        // 추출한 토큰을 authService.reissue() 에 넘겨주세요.
        // 재발급 받은 새로운 RefreshToken 역시 HttpOnly 쿠키로 응답에 세팅해야 합니다.
        
        return null; // TODO: 정상 응답 객체로 교체
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        // Hint: 생성된 세션을 무효화합니다 (session.invalidate()).
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공"));
    }
}
