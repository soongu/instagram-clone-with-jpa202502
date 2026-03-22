package com.example.instagramclone.domain.comment.infrastructure;

import com.example.instagramclone.domain.comment.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Map;
import java.util.Set;

/**
 * 댓글 조회 전용 QueryDSL 커스텀 리포지토리 (FollowRepositoryCustom 과 동일한 위치·역할).
 *
 * <p>Day 14 라이브 코딩에서 구현할 쿼리:
 * <ul>
 *   <li>특정 게시글의 원댓글만 Slice (parent is null, 정렬·Pageable)</li>
 *   <li>특정 원댓글 아래 대댓글만 Slice</li>
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
     */
    Slice<Comment> findRepliesByRootComment(Long postId, Long rootCommentId, Pageable pageable);

    /**
     * 원댓글 id → 대댓글 개수 (현재 페이지의 원댓글 id들에 대해 한 번에 집계).
     */
    Map<Long, Long> countRepliesByRootCommentIds(Set<Long> rootCommentIds);
}
