package com.example.instagramclone.controller.rest;

import com.example.instagramclone.domain.comment.dto.request.CommentCreateRequest;
import com.example.instagramclone.domain.comment.dto.response.CommentCreationResponse;
import com.example.instagramclone.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;

    // 댓글 작성 API
    @PostMapping
    public ResponseEntity<CommentCreationResponse> createComment(
            @PathVariable Long postId
            , @AuthenticationPrincipal String username
            , @RequestBody @Valid CommentCreateRequest commentCreateRequest
    ) {

        CommentCreationResponse response = commentService.createComment(
                postId, username, commentCreateRequest.getContent()
        );

        return ResponseEntity.ok().body(response);
    }

}
