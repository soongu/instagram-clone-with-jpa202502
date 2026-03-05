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

        // TODO: [실습 4] 우리가 만든 JwtAuthenticationFilter를 Spring Security 필터 체인에 등록하세요.
        // 위치: UsernamePasswordAuthenticationFilter.class 이전에 동작하도록 설정해야 합니다.

        return http.build();
    }
}
