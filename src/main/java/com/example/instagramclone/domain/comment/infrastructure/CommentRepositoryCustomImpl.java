package com.example.instagramclone.domain.comment.infrastructure;

import com.example.instagramclone.domain.comment.domain.Comment;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * {@link CommentRepositoryCustom}의 QueryDSL 구현체.
 *
 */
@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Comment> findRootCommentsByPostId(Long postId, Pageable pageable) {
        // TODO Day 14: QComment.comment.post.id.eq(postId).and(QComment.comment.parent.isNull()) ...
        // TODO Day 14: offset/limit + (pageSize+1)로 hasNext 판별 (FollowRepositoryCustomImpl 참고)
        return new SliceImpl<>(Collections.emptyList(), pageable, false);
    }

    @Override
    public Slice<Comment> findRepliesByRootComment(Long postId, Long rootCommentId, Pageable pageable) {
        // TODO Day 14: parent.id == rootCommentId, 동일 post 소속 검증 조건 추가
        return new SliceImpl<>(Collections.emptyList(), pageable, false);
    }

    @Override
    public Map<Long, Long> countRepliesByRootCommentIds(Set<Long> rootCommentIds) {
        // TODO Day 14: parent.id in rootCommentIds group by parent.id, count — 빈 집합이면 빈 Map
        if (rootCommentIds == null || rootCommentIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return new HashMap<>();
    }

}
