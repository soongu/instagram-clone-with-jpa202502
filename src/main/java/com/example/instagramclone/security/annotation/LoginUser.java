package com.example.instagramclone.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controller 메서드의 파라미터로 로그인한 유저의 ID(Long)를 바로 주입받기 위한 커스텀 어노테이션
 * 내부에 @AuthenticationPrincipal 을 품고 있습니다.
 */
// TODO: [과제 1] @AuthenticationPrincipal(expression = ...) 을 사용하여 보안 컨텍스트의 이름을 가져오도록 설정하세요.
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}
