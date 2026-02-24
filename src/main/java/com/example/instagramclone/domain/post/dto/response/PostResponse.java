package com.example.instagramclone.domain.post.dto.response;

import java.time.LocalDateTime;
import java.util.List;

// TODO: [Day 7] API 명세서에 맞추어 피드 응답 DTO를 작성하세요.
// 필드: feed_id, content, username, profileImageUrl, images(List<PostImageResponse>), createdAt, updatedAt, likeStatus, commentCount
public record PostResponse(
        // 빈 레코드
) {
    // TODO: [Day 7] Post 엔티티를 PostResponse DTO로 변환하는 정적 팩토리 메서드(from)를 작성하세요.
    // Hint: likesStatus는 LikeStatusResponse.empty(), commentCount는 0으로 임시 고정합니다.
}
