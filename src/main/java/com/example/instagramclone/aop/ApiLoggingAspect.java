package com.example.instagramclone.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ApiLoggingAspect {

    // TODO: [실습 1] 모든 Controller의 API 요청/응답을 가로채는 Pointcut 설정 범위를 지정하세요.
    @Around("execution(* com.example.instagramclone.controller..*.*(..))")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {

        // TODO: [실습 2] API 요청이 들어왔을 때, 어떤 컨트롤러의 어떤 메서드가 호출되었는지 로그(INFO)로 남기세요.
        
        long startTime = System.currentTimeMillis();

        // 실제 API 컨트롤러 메서드 실행
        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();

        // TODO: [실습 3] API 수행이 완료된 후, 수행 시간(ms)과 정상 응답 완료를 로그(INFO)로 남기세요.
        
        return result;
    }
}
