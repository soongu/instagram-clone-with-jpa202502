package com.example.instagramclone.controller.rest;

import org.springframework.data.domain.Pageable;

import com.example.instagramclone.domain.common.dto.FeedResponse;
import com.example.instagramclone.domain.post.dto.response.PostResponse;

import com.example.instagramclone.domain.post.dto.request.PostCreateRequest;
import com.example.instagramclone.service.PostService;
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
import com.example.instagramclone.util.PageableUtil;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPost(
            @RequestPart("feed") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            // TODO: [실습 4] SessionUser 파라미터를 지우고, @AuthenticationPrincipal 을 사용하여
            // 인증 필터가 넣어준 로그인 멤버의 ID(Long)를 바로 주입받도록 수정하세요.
            // (심화 과제: 이를 어노테이션 자체를 추상화하는 @LoginUser 커스텀 어노테이션으로 교체해 보세요.)
            @SessionAttribute(name = AuthConstants.SESSION_KEY, required = false) SessionUser sessionUser) throws IOException { 
            
        // TODO: [실습 4] 필터가 미인증 접근을 막아주므로, 이 null 체크 방어 로직은 삭제해도 됩니다.
        if (sessionUser == null) {
            throw new MemberException(MemberErrorCode.UNAUTHORIZED_ACCESS); // 401 Unauthorized
        }

        postService.create(request, images, sessionUser.id());
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null));
    }

    // TODO: [Day 7] JSON 무한 순환 참조 에러 체험을 위한 피드 조회 API 작성
    // 처음엔 List<Post> 엔티티 직접 반환으로 무한 순환 에러 체험 -> 이후 FeedResponse<PostResponse> DTO 및 Pageable 적용으로 변경
    @GetMapping
    public ResponseEntity<ApiResponse<FeedResponse<PostResponse>>> getFeed(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            // TODO: [실습 4] 윗 줄의 createPost 와 동일하게 @AuthenticationPrincipal 로 교체하세요.
            @SessionAttribute(name = AuthConstants.SESSION_KEY, required = false) SessionUser sessionUser) {
        
        // TODO: [실습 4] 이 부분도 마찬가지로 필터 도입 후 불필요하므로 삭제하세요.
        if (sessionUser == null) {
            throw new MemberException(MemberErrorCode.UNAUTHORIZED_ACCESS); // 401 Unauthorized
        }

        // 파라미터 검증 및 Pageable 생성 (관심사 분리)
        Pageable pageable = PageableUtil.createSafePageableDesc(page, size, "id");
        
        FeedResponse<PostResponse> response = postService.getFeed(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
