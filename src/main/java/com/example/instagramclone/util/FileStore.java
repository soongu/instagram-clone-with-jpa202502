package com.example.instagramclone.util;

import com.example.instagramclone.exception.PostErrorCode;
import com.example.instagramclone.exception.PostException;
import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs(); // 디렉토리가 없으면 생성
        }
    }

    // TODO: 2. MultipartFile을 받아 로컬 디스크에 저장하고 고유한 파일명(UUID)을 반환하는 메서드를 완성하세요
    public String storeFile(MultipartFile multipartFile) throws IOException {

        // 1. 원본 파일명 추출
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new PostException(PostErrorCode.INVALID_FILE_EXTENSION);
        }

        // 확장자 및 MIME 타입 검증
        String contentType = multipartFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new PostException(PostErrorCode.INVALID_FILE_EXTENSION);
        }

        String lowerOriginal = originalFilename.toLowerCase();
        if (!lowerOriginal.endsWith(".jpg") && !lowerOriginal.endsWith(".jpeg") && !lowerOriginal.endsWith(".png")) {
            throw new PostException(PostErrorCode.INVALID_FILE_EXTENSION);
        }

        // 2. 서버에 저장할 고유 파일명 생성 (UUID 활용)
        int extIndex = originalFilename.lastIndexOf(".");
        String ext = (extIndex == -1) ? "" : originalFilename.substring(extIndex);
        String storeFileName = UUID.randomUUID().toString() + ext;

        // 3. 전체 파일 저장 경로 문자열 만들기 (fileDir + 고유파일명)
        String fullPath = fileDir + storeFileName;

        // 4. 로컬 디스크에 파일 저장 (multipartFile.transferTo() 활용)
        multipartFile.transferTo(new File(fullPath));

        // 5. 클라이언트가 접근할 수 있는 정적 리소스 경로 구조로 반환 (WebMvcConfig 활용)
        return "/images/" + storeFileName;
    }
}
