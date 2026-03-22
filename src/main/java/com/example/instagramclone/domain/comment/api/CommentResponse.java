package com.example.instagramclone.domain.comment.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * 댓글 목록 응답 (원댓글 목록에서 대댓글 수 포함).
 *
 * <p>대댓글 전용 조회 API에서는 {@code replyCount}를 null로 두거나 스펙에서 제외해도 됨 (팀 합의).
 */
public record CommentResponse(
        Long id,
        String content,
        @JsonProperty("member_id")
        Long memberId,
        String username,
        String profileImageUrl,
        /** 원댓글 목록에서만 채움. 대댓글 목록이면 null */
        Integer replyCount,
        LocalDateTime createdAt
) {
}
