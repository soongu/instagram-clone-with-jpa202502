package com.example.instagramclone.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // TODO: 1. CSRF 비활성화 및 기본 보안 설정을 하세요
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        // HTTP 요청에 대한 보안 인가 설정
        http.authorizeHttpRequests(auth -> auth
                // FORWARD 방식의 요청(내부 포워딩)은 모두 허용
                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                // 인증 없이 접근 가능한 공개 경로 설정
                .requestMatchers(
                        "/",                // 메인 페이지
                        "/signup",          // 회원가입 페이지
                        "/api/auth/**",     // 인증 관련 API 엔드포인트
                        "/error",           // 에러 페이지
                        "/css/**",          // 정적 리소스 (CSS)
                        "/js/**",           // 정적 리소스 (JavaScript)
                        "/images/**",       // 정적 리소스 (이미지)
                        "/favicon.ico"      // 파비콘
                ).permitAll()
                // 그 외 모든 요청은 인증된 사용자만 접근 가능
                .anyRequest().authenticated()
        );

        return http.build();
    }
}
