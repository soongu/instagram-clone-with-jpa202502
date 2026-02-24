package com.example.instagramclone.controller.rest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    // TODO: [Day 7] JSON 무한 순환 참조 에러 체험을 위한 피드 조회 API 작성
    // 처음엔 List<Post> 엔티티 직접 반환으로 무한 순환 에러 체험 -> 이후 FeedResponse<PostResponse> DTO 및 Pageable 적용으로 변경
    @GetMapping
    public ResponseEntity<ApiResponse<FeedResponse<PostResponse>>> getFeed(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @SessionAttribute(name = AuthConstants.SESSION_KEY, required = false) SessionUser sessionUser) {
        
        // 1. 요청 인가(Authorization): 세션에서 추출한 sessionUser가 없는 경우 예외 발생시켜 접근 제한.
        if (sessionUser == null) {
            throw new MemberException(MemberErrorCode.UNAUTHORIZED_ACCESS); // 401 Unauthorized
        }

        // 프론트엔드 페이지는 1부터 시작하지만 (feed.js - 10번라인 참조), Spring Data JPA는 0-index 기반이므로 1을 빼서 PageRequest 생성
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        
        FeedResponse<PostResponse> response = postService.getFeed(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }

}
