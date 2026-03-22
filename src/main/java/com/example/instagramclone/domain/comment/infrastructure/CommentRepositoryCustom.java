package com.example.instagramclone.domain.comment.infrastructure;

import com.example.instagramclone.domain.comment.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Map;
import java.util.Set;

/**
 * 댓글 조회 전용 QueryDSL 커스텀 리포지토리 (FollowRepositoryCustom 과 동일한 위치·역할).
 *
 * <p>Day 14에서 다루는 쿼리:
 * <ul>
 *   <li>특정 게시글의 원댓글만 Slice (parent is null, 정렬·Pageable)</li>
 *   <li>특정 원댓글 아래 대댓글만 Slice — {@link #findRepliesByRootComment(Long, Long, Pageable)}</li>
 *   <li>대댓글 API 선검증 — {@link #existsRootCommentForReplies(Long, Long)} (id + post + 원댓글 여부)</li>
 *   <li>원댓글 id 집합에 대한 replyCount 일괄 집계 (N+1 방지)</li>
 * </ul>
 */
public interface CommentRepositoryCustom {

    /**
     * 게시글의 원댓글 목록 (무한 스크롤 / Slice).
     */
    Slice<Comment> findRootCommentsByPostId(Long postId, Pageable pageable);

    /**
     * 원댓글에 달린 대댓글 목록 (더보기 / Slice).
     *
     * <p>조건: {@code post_id = postId} 이고 {@code parent_id = rootCommentId}.
     * 호출 전 {@link #existsRootCommentForReplies(Long, Long)} 로 원댓글·게시글 일치를 검증하는 것을 권장합니다.
     */
    Slice<Comment> findRepliesByRootComment(Long postId, Long rootCommentId, Pageable pageable);

    /**
     * {@code GET .../comments/{rootCommentId}/replies} 선검증용.
     * <p>{@code rootCommentId} 가 해당 {@code postId} 게시글에 속하고, {@code parent IS NULL} 인 원댓글인지 한 번에 판별합니다.
     * (다른 글의 댓글 id, 대댓글 id를 넣은 경우 모두 {@code false})
     */
    boolean existsRootCommentForReplies(Long postId, Long rootCommentId);

    /**
     * 원댓글 id → 대댓글 개수 (현재 페이지의 원댓글 id들에 대해 한 번에 집계).
     */
    Map<Long, Long> countRepliesByRootCommentIds(Set<Long> rootCommentIds);
}
