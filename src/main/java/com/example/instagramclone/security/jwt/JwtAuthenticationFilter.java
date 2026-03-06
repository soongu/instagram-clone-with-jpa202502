package com.example.instagramclone.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import com.example.instagramclone.constant.AuthConstants;
import com.example.instagramclone.security.dto.LoginUserInfoDto;

/**
 * [모든 요청의 첫 번째 검문소, JwtAuthenticationFilter]
 * 
 * Q. 왜 스프링의 Interceptor가 아니라 서블릿의 Filter를 사용할까요?
 * A. 방어의 "최전선"이기 때문입니다. 
 *    Interceptor는 스프링 MVC(DispatcherServlet) 내부로 들어온 이후에 동작합니다.
 *    악성 요청이나 미인증 요청을 스프링 내부까지 들어오게 허용하면 리소스가 낭비되고 공격 표면이 넓어집니다.
 *    따라서 "가장 바깥쪽 문"인 서블릿 Filter 단에서 원천 차단하는 것이 현업의 표준 보안 프랙티스입니다.
 * 
 * Q. 왜 Filter 대신 OncePerRequestFilter를 상속받나요?
 * A. 내부 포워딩(동일 서버 내 다른 컨트롤러로 요청 전달 등)이 발생할 때 
 *    일반 Filter는 불필요하게 두 번 이상 실행될 수 있습니다. 
 *    OncePerRequestFilter는 한 요청(Request) 당 정확히 한 번만 검문하도록 보장합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {

        // 1. Request Header 에서 클라이언트가 보낸 JWT 토큰을 가로채기
        String token = resolveToken(request);

        // 2. 가로챈 토큰이 존재하고(null이 아니고), 위변조 및 만료되지 않은 "유효한" 토큰인지 검사
        // 실무 포인트: StringUtils.hasText()는 null, 빈 문자열(""), 공백("   ")을 한 번에 걸러주는 스프링 필수 유틸입니다.
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            
            // 3. 유효한 토큰이면 Spring Security Context 에 인증 정보 심기
            // 실무 포인트: 매 API 요청마다 DB나 세션을 조회하지 않습니다. (Stateless)
            // 토큰 자체에 들어있는 Payload(Claims) 정보만으로 주체(Principal)를 확인합니다.
            Long memberId = jwtTokenProvider.getMemberId(token);
            String role = jwtTokenProvider.getRole(token);
            
            // 토큰에서 추출한 정보로 Authentication(인증 도장) 객체 생성
            // Principal(주체)로 memberId를 직접 넣지 않고, 확장성을 위해 LoginUserInfoDto DTO를 넣습니다.
            // Credentials(비밀번호)는 null 처리, Authorities(권한) 부여
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    LoginUserInfoDto.builder().id(memberId).build(), 
                    null, 
                    Collections.singletonList(new SimpleGrantedAuthority(StringUtils.hasText(role) ? role : "ROLE_USER")) // 토큰에서 추출한 권한 사용, 없으면 기본값
            );
            
            // SecurityContextHolder의 Context(스프링 시큐리티의 '임시 보안 명부')에 생성한 Authentication 객체 저장 
            // 이렇게 등록해두면 이후의 컨트롤러에서 @AuthenticationPrincipal 로 memberId를 바로 꺼내 쓸 수 있습니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다. (uri: {})", memberId, request.getRequestURI());
        }

        // 4. 다음 검문소(필터)로 요청 넘기기
        // 주의: 토큰이 없거나 유효하지 않아도 여기서 에러를 내지 않고 일단 넘깁니다. 
        // 인증정보(도장)가 없는 상태로 넘어가면, 이어지는 인가(Authorization) 필터가 알아서 401(Unauthorized)로 튕겨냅니다.
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP Header 에서 토큰 값만 순수하게 추출하는 헬퍼 메서드
     * 보통 클라이언트는 "Authorization: Bearer eyJhbGci..." 형태로 토큰을 보냅니다.
     */
    private String resolveToken(HttpServletRequest request) {
        // "Authorization" 헤더 값을 가져옵니다.
        String bearerToken = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
        
        // 가져온 값이 "Bearer " 로 시작한다면, 앞의 7글자("Bearer ")를 잘라내고 순수 토큰 문자열만 반환합니다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AuthConstants.BEARER_PREFIX)) {
            return bearerToken.substring(AuthConstants.BEARER_PREFIX.length()); // "Bearer " 길이만큼 잘라냄
        }
        
        return null; // 토큰이 없거나 규격에 맞지 않으면 null 반환
    }
}
