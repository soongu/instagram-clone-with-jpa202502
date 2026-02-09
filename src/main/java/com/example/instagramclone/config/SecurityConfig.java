package com.example.instagramclone.config;

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

        // TODO: 2. 인증/인가 규칙을 설정하세요 (authorizeHttpRequests)
        // /api/auth/** 패턴은 허용하고, 나머지는 인증 필요하게 설정

        return http.build();
    }
}
