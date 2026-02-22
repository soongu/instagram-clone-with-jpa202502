package com.example.instagramclone.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class FileStore {

    // TODO: 1. application.yml의 file.upload.location 값을 주입받으세요 (@Value 활용)
    @Value("${file.upload.location}")
    private String fileDir;

    // TODO: 2. MultipartFile을 받아 로컬 디스크에 저장하고 고유한 파일명(UUID)을 반환하는 메서드를 완성하세요
    public String storeFile(MultipartFile multipartFile) throws IOException {
        if (multipartFile.isEmpty()) {
            return null;
        }

        // 1. 원본 파일명 추출
        
        // 2. 서버에 저장할 고유 파일명 생성 (UUID 활용)
        
        // 3. 전체 파일 저장 경로 문자열 만들기 (fileDir + 고유파일명)
        
        // 4. 로컬 디스크에 파일 저장 (multipartFile.transferTo() 활용)
        
        // 5. 만들어진 고유 파일명 반환
        return null;
    }
}
