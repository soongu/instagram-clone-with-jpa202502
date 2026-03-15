package com.example.instagramclone.domain.post.api;

import com.example.instagramclone.core.common.dto.ApiResponse;
import com.example.instagramclone.core.common.dto.FeedResponse;
import com.example.instagramclone.core.util.PageableUtil;
import com.example.instagramclone.infrastructure.security.annotation.LoginUser;
import com.example.instagramclone.domain.post.application.PostLikeService;
import com.example.instagramclone.domain.post.application.PostService;
import com.example.instagramclone.infrastructure.security.dto.LoginUserInfoDto;
import org.springframework.data.domain.Pageable;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostLikeService postLikeService;

    @PostMapping("/api/posts")
    public ResponseEntity<ApiResponse<PostCreateResponse>> createPost(
            @RequestPart("feed") PostCreateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @LoginUser LoginUserInfoDto loginUser) throws IOException {

        // 필터가 앞에서 다 막아주기 때문에,
        Long postId = postService.create(request, images, loginUser.id());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(PostCreateResponse.from(postId)));
    }

    @GetMapping("/api/posts")
    public ResponseEntity<ApiResponse<FeedResponse<PostResponse>>> getFeed(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            // 인증 데이터가 필요 없는 Public 피드일 경우 LoginUserArgumentResolver가 null을 반환하도록 설계했습니다.
            @LoginUser LoginUserInfoDto loginUser) {

        // 파라미터 검증 및 Pageable 생성 (관심사 분리)
        Pageable pageable = PageableUtil.createSafePageableDesc(page, size, "id");

        FeedResponse<PostResponse> response = postService.getFeed(pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // URL이 /members/...로 시작하지만, 반환 리소스가 Post이므로 PostController에서 처리합니다.
    @GetMapping("/api/members/{memberId}/posts")
    public ResponseEntity<ApiResponse<FeedResponse<ProfilePostResponse>>> getMemberPosts(
            @PathVariable Long memberId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "12") int size) {

        Pageable pageable = PageableUtil.createSafePageableDesc(page, size, "id");

        FeedResponse<ProfilePostResponse> response = postService.getMemberPosts(memberId, pageable);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * [Day 12 Step 2] TODO: 좋아요 토글 API 구현
     * POST /api/posts/{postId}/likes 호출 시 추가/취소 토글.
     * 로그인 사용자(loginUser.id())와 postId를 PostLikeService.toggleLike()에 전달하고,
     * LikeStatusResponse(liked, likeCount)를 반환합니다.
     */
    @PostMapping("/api/posts/{postId}/likes")
    public ResponseEntity<ApiResponse<LikeStatusResponse>> toggleLike(
            @PathVariable Long postId,
            @LoginUser LoginUserInfoDto loginUser) {
        LikeStatusResponse response = postLikeService.toggleLike(loginUser.id(), postId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
