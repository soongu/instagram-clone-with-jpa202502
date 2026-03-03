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

    // TODO: [Live Coding] 다양한 로그 레벨을 테스트해보기 위한 임시 엔드포인트
    @GetMapping("/test-log")
    public ResponseEntity<String> testLogging() {
        log.trace("👉 [TRACE] 가장 상세한 로그. 개발 환경에서도 잘 안 켭니다.");
        log.debug("👉 [DEBUG] 개발 환경에서 디버깅용으로 남기는 로그입니다.");
        log.info("👉 [INFO] 운영 환경에서 남겨야 할 중요한 비즈니스 정보입니다.");
        log.warn("👉 [WARN] 시스템에 문제는 없지만, 주의해야 할 상황입니다. (예: 로그인 5회 실패)");
        log.error("👉 [ERROR] 치명적인 에러가 발생했습니다. (예: DB 연결 끊김, 결제 실패)");

        return ResponseEntity.ok("로그가 출력되었습니다! 콘솔창을 확인해보세요.");
    }

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
