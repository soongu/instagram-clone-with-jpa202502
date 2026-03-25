package com.example.instagramclone.domain.post.api;

import com.example.instagramclone.domain.member.api.MemberSummary;
import com.example.instagramclone.domain.post.domain.Post;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * 피드 상세 조회 응답 DTO
 *
 * <p>Day 15 라이브 코딩에서 다룰 내용
 * <ul>
 * <li>게시물 본문, 이미지 캐러셀, 작성자 요약은 한 응답에 포함</li>
 * <li>댓글 목록은 포함하지 않음 (별도 API 호출 전제)</li>
 * <li>{@code prevPostId}, {@code nextPostId}: 진입 컨텍스트에 따른 네비게이션 식별자</li>
 * </ul>
 *
 * <p>record로 변환 후 접근자는 {@code postId()}, {@code content()}, {@code writer()} 형태가 됩니다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PostDetailResponse(
        Long postId,
        String content,
        MemberSummary writer,
        List<String> imageUrls,
        // 네비게이션 옵션 필드 (컨텍스트가 없거나 맨 앞/뒤면 null)
        Long prevPostId,
        Long nextPostId
) {

    public static PostDetailResponse of(Post post, MemberSummary writer, List<String> imageUrls, Long prevPostId, Long nextPostId) {
        return new PostDetailResponse(
                post.getId(),
                post.getContent(),
                writer,
                imageUrls,
                prevPostId,
                nextPostId
        );
    }
}
