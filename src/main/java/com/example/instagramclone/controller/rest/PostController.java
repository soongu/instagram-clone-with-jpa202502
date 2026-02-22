package com.example.instagramclone.controller.rest;

import com.example.instagramclone.domain.post.dto.request.PostCreateRequest;
import com.example.instagramclone.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // TODO: 1. 피드 작성 API를 구현하세요 (@PostMapping)
    // Hint: MultipartFile을 포함하므로 @ModelAttribute 또는 @RequestPart를 사용할 수 있습니다.
    
    // TODO: 2. 피드 목록 조회 API를 구현하세요 (@GetMapping)
    // Hint: 서비스 계층에서 조회된 결과를 반환하며, N+1 문제 발생 로그를 확인합니다.

}
