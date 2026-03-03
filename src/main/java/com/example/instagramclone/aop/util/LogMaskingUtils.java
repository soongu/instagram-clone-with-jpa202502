package com.example.instagramclone.aop.util;

import org.springframework.util.StringUtils;

import com.example.instagramclone.aop.annotation.Masking;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class LogMaskingUtils {

    /**
     * 파라미터 배열을 순회하면서 객체 내부의 @Masking 필드를 찾아 가려주는 문자열을 반환합니다.
     */
    public static String buildMaskedParamsString(String[] parameterNames, Object[] args) {
        if (args == null || args.length == 0) {
            return "없음";
        }

        List<String> paramList = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            String paramName = parameterNames != null ? parameterNames[i] : "arg" + i;
            Object arg = args[i];

            if (arg == null) {
                paramList.add(paramName + "=null");
                continue;
            }

            // DTO 객체인 경우 (자바 기본형, String 등이 아닌 경우) 리플렉션으로 내부 필드 검사
            if (!isWrapperType(arg.getClass()) && arg.getClass() != String.class) {
                String maskedObjectString = applyMaskingToObject(arg);
                paramList.add(paramName + "=" + maskedObjectString);
            } else {
                paramList.add(paramName + "=" + arg);
            }
        }
        return StringUtils.collectionToCommaDelimitedString(paramList);
    }

    /**
     * DTO 객체의 필드들을 리플렉션으로 읽어 @Masking 애노테이션이 있으면 "******"로 덮어씁니다.
     */
    private static String applyMaskingToObject(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        List<String> fieldStrings = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                // 필드에 @Masking 애노테이션이 붙어있다면 값 숨김 처리
                if (field.isAnnotationPresent(Masking.class) && value != null) {
                    fieldStrings.add(field.getName() + "='******'");
                } else {
                    fieldStrings.add(field.getName() + "=" + value);
                }
            } catch (IllegalAccessException e) {
                fieldStrings.add(field.getName() + "=ERROR");
            }
        }
        return clazz.getSimpleName() + "{" + StringUtils.collectionToCommaDelimitedString(fieldStrings) + "}";
    }

    /**
     * 원시(Primitive) 타입 래퍼 클래스인지 확인하는 유틸리티 메서드
     */
    private static boolean isWrapperType(Class<?> clazz) {
        return clazz == Boolean.class || clazz == Character.class ||
               clazz == Byte.class || clazz == Short.class ||
               clazz == Integer.class || clazz == Long.class ||
               clazz == Float.class || clazz == Double.class;
    }
}
