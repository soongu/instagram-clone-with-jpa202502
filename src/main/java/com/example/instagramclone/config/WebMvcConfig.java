package com.example.instagramclone.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // TODO: 1. application.yml의 file.upload.location 값을 주입받으세요 (@Value 활용)
    @Value("${file.upload.location}")
    private String fileDir;

    // TODO: 2. 정적 리소스 경로 매핑을 추가하세요 (업로드된 이미지 접근용)
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Hint: "/images/**" URL 요청을 물리적 디렉토리 "file:..." 위치로 연결합니다.
        // registry.addResourceHandler("/images/**").addResourceLocations("file:" + fileDir);
    }
}
