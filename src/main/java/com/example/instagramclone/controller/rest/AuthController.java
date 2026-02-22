package com.example.instagramclone.controller.rest;

import com.example.instagramclone.constant.AuthConstants;
import com.example.instagramclone.domain.common.dto.ApiResponse;
import com.example.instagramclone.domain.member.dto.request.LoginRequest;
import com.example.instagramclone.domain.member.dto.request.SignUpRequest;
import com.example.instagramclone.domain.member.dto.response.DuplicateCheckResponse;
import com.example.instagramclone.domain.member.dto.response.SessionUser;
import com.example.instagramclone.domain.member.dto.response.SignUpResponse;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
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

    // TODO: 1. 회원가입 API를 구현하세요 (@PostMapping)
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        memberService.signUp(signUpRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(SignUpResponse.of(signUpRequest.username(), AuthConstants.SIGNUP_SUCCESS_MESSAGE)));
    }

    // TODO: 2. 중복 확인 API를 구현하세요 (@GetMapping)
    @GetMapping("/check-duplicate")
    public ResponseEntity<ApiResponse<DuplicateCheckResponse>> checkDuplicate(@RequestParam String type, @RequestParam String value) {
        boolean isAvailable = memberService.checkDuplicate(type, value);
        String message = isAvailable ? "사용 가능한 " + type + "입니다." : "이미 사용 중인 " + type + "입니다.";

        DuplicateCheckResponse response = isAvailable ?
                DuplicateCheckResponse.available(message) :
                DuplicateCheckResponse.unavailable(message);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // TODO: 3. 로그인 API를 구현하세요 (@PostMapping("/login"))
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<SessionUser>> login(@RequestBody @Valid LoginRequest loginRequest, HttpServletRequest request) {
        // Hint: MemberService.login() 호출 후 결과 Member를 통해 세션 생성.
        SessionUser sessionUser = memberService.login(loginRequest);

        HttpSession session = request.getSession();
        session.setAttribute(AuthConstants.SESSION_KEY, sessionUser);

        return ResponseEntity.ok(ApiResponse.success(sessionUser));
    }

    // TODO: 4. 로그아웃 API를 구현하세요 (@PostMapping("/logout"))
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
