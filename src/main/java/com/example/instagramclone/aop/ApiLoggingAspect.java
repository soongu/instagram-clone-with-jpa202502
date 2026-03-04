package com.example.instagramclone.aop;

import com.example.instagramclone.aop.util.LogMaskingUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jboss.logging.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;

@Slf4j
@Aspect
@Component
public class ApiLoggingAspect {

    // TODO: [실습 1] 모든 Controller의 API 요청/응답을 가로채는 Pointcut 설정 범위를 지정하세요.
    @Around("execution(* com.example.instagramclone.controller.rest.*.*(..))")
    public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {

        // [과제 1 예시답안] 고유한 Trace ID 생성 (너무 기니까 앞 8자리만 자릅니다)
        String traceId = UUID.randomUUID().toString().substring(0, 8);

        // [과제 1 예시답안] 현재 스레드의 MDC 컨텍스트에 Trace ID 저장
        MDC.put("traceId", traceId);

        try {
            // 1. 요청 정보 추출 (어떤 컨트롤러의 어떤 메서드인지)
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String methodName = joinPoint.getSignature().getName();

            // 2. 파라미터 정보 및 이름 추출 준비
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            // DTO가 아닌 로깅하기 부적절한 객체(MultipartFile, HttpServletRequest 등) 필터링
            List<String> filteredParamNames = new ArrayList<>();
            List<Object> filteredArgs = new ArrayList<>();

            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                if (!isLoggable(arg)) {
                    continue; // 로깅 제외
                }
                filteredParamNames.add(parameterNames != null ? parameterNames[i] : "arg" + i);
                filteredArgs.add(arg);
            }

            // 파라미터 [이름=값] 형태의 문자열 생성 및 마스킹 처리 (SRP 적용하여 별도 유틸로 분리)
            String paramsString = LogMaskingUtils.buildMaskedParamsString(
                    filteredParamNames.toArray(new String[0]), 
                    filteredArgs.toArray()
            );

            // 3. 요청 로그 출력
            log.info("[API 요청] {} / {} | 파라미터: [{}]", className, methodName, paramsString);

            long startTime = System.currentTimeMillis();

            // 실제 API 컨트롤러 메서드 실행
            Object result = joinPoint.proceed();

            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;

            // 3. 응답 로그 출력 (수행 시간 포함)
            log.info("[API 응답] {} / {} (수행시간: {}ms)", className, methodName, executionTime);
            return result;
        } finally {
            // [과제 1 예시답안] finally 블록에서 MDC 컨텍스트 제거 (스레드 풀 재사용 시 필수!)
            MDC.remove("traceId");
        }
    }

    private boolean isLoggable(Object arg) {
        if (arg == null) return true;

        Class<?> clazz = arg.getClass();

        // 1. 기본 타입, 래퍼 클래스, String 허용
        if (isWrapperType(clazz) || clazz == String.class) {
            return true;
        }

        // 2. 패키지 기반: 우리가 만든 객체(com.example.instagramclone.**)만 허용
        String packageName = clazz.getPackageName();
        if (packageName.startsWith("com.example.instagramclone")) {
            return true;
        }

        // 3. 컬렉션 타입인 경우 내부 요소 검사
        if (arg instanceof Collection<?> collection) {
            for (Object item : collection) {
                if (item != null) {
                    Class<?> itemClass = item.getClass();
                    if (!isWrapperType(itemClass) && itemClass != String.class &&
                        !itemClass.getPackageName().startsWith("com.example.instagramclone")) {
                        return false; // 컬렉션 내부에 허용되지 않은 타입이 있으면 전체 제외
                    }
                }
            }
            return true; // 빈 컬렉션이거나 모두 허용되는 타입인 경우
        }

        // 그 외 (MultipartFile, HttpServletRequest, 외부 라이브러리 객체 등) 모두 제외
        return false;
    }

    private boolean isWrapperType(Class<?> clazz) {
        return clazz == Boolean.class || clazz == Character.class ||
               clazz == Byte.class || clazz == Short.class ||
               clazz == Integer.class || clazz == Long.class ||
               clazz == Float.class || clazz == Double.class;
    }
}
