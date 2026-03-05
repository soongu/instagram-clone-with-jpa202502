package com.example.instagramclone.config;

import com.example.instagramclone.security.jwt.JwtAuthenticationFilter;
import com.example.instagramclone.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

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
                .anyRequest().permitAll() 
            )
            // Step 2: "SecurityContext 에 신분증 걸어두기" (Filter 등록)
            // 우리가 직접 만든 JwtAuthenticationFilter 객체를 생성하여 필터 체인에 끼워 넣습니다.
            // Q. 왜 UsernamePasswordAuthenticationFilter '앞(Before)'에 넣나요?
            // A. 스프링 시큐리티의 기본 인증 동작(폼 로그인 시 유저네임/비번 검사)이 일어나기 전에, 
            //    우리가 가로챈 JWT 토큰이 유효하다면 "이 사람은 이미 통과!"라고 인증 도장(Authentication)을 
            //    미리 쾅 찍어주기 위해서입니다. 
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider), 
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
