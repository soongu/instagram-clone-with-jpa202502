package com.example.instagramclone.domain.post.api;

import com.example.instagramclone.domain.member.api.MemberSummary;
import com.example.instagramclone.domain.post.domain.Post;
import lombok.Builder;
import lombok.Getter;

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
 */
@Getter
@Builder
public class PostDetailResponse {

    private Long postId;
    private String content;
    private MemberSummary writer;
    private List<String> imageUrls;

    // 네비게이션 옵션 필드 (컨텍스트가 없거나 맨 앞/뒤면 null)
    private Long prevPostId;
    private Long nextPostId;

    public static PostDetailResponse of(Post post, MemberSummary writer, List<String> imageUrls, Long prevPostId, Long nextPostId) {
        return PostDetailResponse.builder()
                .postId(post.getId())
                .content(post.getContent())
                .writer(writer)
                .imageUrls(imageUrls)
                .prevPostId(prevPostId)
                .nextPostId(nextPostId)
                .build();
    }
}
