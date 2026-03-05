package com.example.instagramclone.controller.rest;

import org.springframework.data.domain.Pageable;

import com.example.instagramclone.domain.post.dto.response.PostCreateResponse;
import com.example.instagramclone.domain.post.dto.response.PostResponse;

import com.example.instagramclone.domain.post.dto.request.PostCreateRequest;
import com.example.instagramclone.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.instagramclone.domain.common.dto.ApiResponse;
import com.example.instagramclone.domain.common.dto.FeedResponse;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import com.example.instagramclone.util.PageableUtil;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<PostCreateResponse>> createPost(
            @RequestPart("feed") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            // Step 4: "@AuthenticationPrincipal 로 우아하게 유저 정보 받기"
            // Q. 방금까지 있던 @SessionAttribute 랑 지저분한 null 체크 로직은 어디 갔나요?
            // A. 우리가 방금 만든 '보안 요원(JwtAuthenticationFilter)'이 정상적인 토큰을 확인하면,
            //    SecurityContextHolder(게시판)에 `memberId`를 적어두고 통과시킵니다.
            //    스프링 시큐리티의 @AuthenticationPrincipal 은 그 게시판을 확인해서 
            //    여기에 `memberId`를 "주사기처럼 쏙!" 꽂아주는 마법의 어노테이션입니다!
            //    덕분에 컨트롤러는 "토큰이 정상인가?" 에 대한 고민을 1도 안 해도 됩니다. 완전 깔끔하죠?
            @AuthenticationPrincipal Long memberId) throws IOException { 
            
        // 필터가 앞에서 다 막아주기 때문에, 
        Long postId = postService.create(request, images, memberId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(PostCreateResponse.from(postId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<FeedResponse<PostResponse>>> getFeed(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            // Step 4 복습: 피드 조회도 마찬가지로 @AuthenticationPrincipal 을 사용합니다.
            @AuthenticationPrincipal Long memberId) {

        // 파라미터 검증 및 Pageable 생성 (관심사 분리)
        Pageable pageable = PageableUtil.createSafePageableDesc(page, size, "id");
        
        FeedResponse<PostResponse> response = postService.getFeed(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
