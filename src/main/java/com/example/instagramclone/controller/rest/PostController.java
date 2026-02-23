package com.example.instagramclone.controller.rest;

import com.example.instagramclone.domain.post.dto.request.PostCreateRequest;
import com.example.instagramclone.service.PostService;
import com.example.instagramclone.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.instagramclone.constant.AuthConstants;
import com.example.instagramclone.domain.common.dto.ApiResponse;
import com.example.instagramclone.domain.member.dto.response.SessionUser;
import com.example.instagramclone.exception.MemberErrorCode;
import com.example.instagramclone.exception.MemberException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPost(
            @RequestPart("feed") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @SessionAttribute(name = AuthConstants.SESSION_KEY, required = false) SessionUser sessionUser) throws IOException { 
            
        if (sessionUser == null) {
            throw new MemberException(MemberErrorCode.UNAUTHORIZED_ACCESS); // 401 Unauthorized
        }

        postService.create(request, images, sessionUser.id());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }

    // ------------------------------------------------------------------------------------------------
    // [임시 테스트 API 시작] - Step 2 파일 업로드 기능만 단독으로 테스트해보기 위함 (DB 저장 안함)
    // ------------------------------------------------------------------------------------------------
    private final FileStore fileStore;

    // 임시 테스트 1. 단일 파일 업로드
    @PostMapping("/test/upload/single")
    public String testSingleUpload(@RequestParam("file") MultipartFile file) throws IOException {
        String savedFileName = fileStore.storeFile(file);
        return "단일 파일 업로드 성공! 서버에 저장된 고유 파일명: " + savedFileName;
    }

    // 임시 테스트 2. 다중 파일 업로드
    @PostMapping("/test/upload/multi")
    public String testMultiUpload(@RequestParam("files") List<MultipartFile> files) throws IOException {
        StringBuilder result = new StringBuilder("다중 파일 업로드 성공! \n[저장된 파일명 목록]\n");
        for (MultipartFile file : files) {
            String savedFileName = fileStore.storeFile(file);
            result.append("- ").append(savedFileName).append("\n");
        }
        return result.toString();
    }
    // ------------------------------------------------------------------------------------------------
    // [임시 테스트 API 끝]
    // ------------------------------------------------------------------------------------------------
}
