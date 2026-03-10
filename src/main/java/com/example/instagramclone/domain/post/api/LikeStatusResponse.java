package com.example.instagramclone.domain.post.dto.response;

// 피드 좋아요 상태 응답 DTO (현재는 연습용으로 고정값 반환)
public record LikeStatusResponse(
        boolean liked,
        int likeCount
) {
    public static LikeStatusResponse empty() {
        return new LikeStatusResponse(false, 0);
    }
}
