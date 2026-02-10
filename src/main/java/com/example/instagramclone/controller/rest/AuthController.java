package com.example.instagramclone.controller.rest;

import com.example.instagramclone.domain.member.dto.request.SignUpRequest;
import com.example.instagramclone.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;

    // TODO: 1. 회원가입 API를 구현하세요 (@PostMapping)
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        try {
            memberService.signUp(signUpRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
        } catch (IllegalStateException e) {
            // Service에서 던진 예외를 Controller에서 직접 잡아서 처리 (Bad Practice 체험)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // TODO: 2. 중복 확인 API를 구현하세요 (@GetMapping)
}
