package com.example.instagramclone.domain.post.api;

import com.example.instagramclone.domain.post.domain.Post;
import com.example.instagramclone.domain.post.domain.PostImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;

// 프론트엔드 스펙에 맞추어 피드 응답 DTO 작성 (feed.js 116번 라인 참조)
public record PostResponse(
        @JsonProperty("feed_id")
        Long id,
        String content,
        String username,
        String profileImageUrl,
        List<PostImageResponse> images,
        LocalDateTime createdAt,
        LikeStatusResponse likeStatus,
        int commentCount
) {
    public static PostResponse of(Post post, List<PostImage> images) {
        return new PostResponse(
                post.getId(),
                post.getContent(),
                post.getWriter().getUsername(),
                post.getWriter().getProfileImageUrl(),
                images.stream()
                        .map(PostImageResponse::from)
                        .toList(),
                post.getCreatedAt(),
                LikeStatusResponse.empty(),
                0
        );
    }
}
