package com.example.instagramclone.controller.rest;

import com.example.instagramclone.domain.member.dto.request.SignUpRequest;
import com.example.instagramclone.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest) {
        return ResponseEntity.ok().build();
    }

    // TODO: 2. 중복 확인 API를 구현하세요 (@GetMapping)
}
