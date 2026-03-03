package com.example.instagramclone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화, 폼 로그인 비활성화, 세션 생성 정책(Stateless) 설정 등
        // Day 8 강의에서 상세 내용을 추가할 예정입니다.
        
        http
            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (JWT를 사용하므로 불필요)
            .formLogin(form -> form.disable()) // 기본 폼 로그인 비활성화
            .httpBasic(basic -> basic.disable()) // 기본 HTTP Basic 인증 비활성화
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않음 (Stateless)
            )
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // 임시로 모든 요청 허용 (실습에서 변경 예정)
            );

        return http.build();
    }
}
