package com.example.instagramclone.domain.comment.infrastructure;

import com.example.instagramclone.domain.comment.domain.Comment;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.example.instagramclone.domain.comment.domain.QComment.comment;

/**
 * {@link CommentRepositoryCustom}의 QueryDSL 구현체.
 *
 * <p>원댓글 목록은 {@code parent IS NULL}, 대댓글 수 집계는 {@code parent_id IN (...)} 그룹으로 N+1 없이 처리합니다.
 */
@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 특정 게시글의 원댓글만 조회합니다. 작성자(Member)는 {@code fetchJoin}으로 함께 로딩해 N+1을 피합니다.
     *
     * <p>정렬: <strong>시간순(오래된 댓글 먼저)</strong> — {@code createdAt ASC}, 동일 시각·배치 삽입 시 순서 고정을 위해 {@code id ASC} 보조 정렬.
     * (메인 피드 글 목록의 최신순 DESC와 달리, 댓글은 대화 스레드를 위에서 아래로 읽는 UX에 맞춤.)
     * Slice 판별: {@code limit = pageSize + 1} 로 한 건 더 가져와 {@code hasNext} 를 결정합니다.
     */
    @Override
    public Slice<Comment> findRootCommentsByPostId(Long postId, Pageable pageable) {
        List<Comment> content = queryFactory
                .selectFrom(comment)
                .join(comment.writer).fetchJoin()
                .where(
                        comment.post.id.eq(postId),
                        comment.parent.isNull()
                )
                .orderBy(comment.createdAt.asc(), comment.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        return toSlice(content, pageable);
    }

    /**
     * 원댓글 id 집합에 대해, 각 원댓글을 {@code parent}로 갖는 대댓글 행 수를 한 번에 집계합니다.
     *
     * <p>집계 결과에 없는 원댓글 id는 서비스에서 0으로 간주합니다 (대댓글 0개).
     */
    @Override
    public Map<Long, Long> countRepliesByRootCommentIds(Set<Long> rootCommentIds) {
        if (rootCommentIds == null || rootCommentIds.isEmpty()) {
            return Collections.emptyMap();
        }

        /* 
        SELECT parent_id, COUNT(id)
        FROM comment
        WHERE parent_id IN ( ... )
        GROUP BY parent_id;
        */
        // reply 행: parent_id = 원댓글 id. parent IS NOT NULL 인 행만 대상이 됨.
        List<Tuple> tuples = queryFactory
                .select(comment.parent.id, comment.id.count())
                .from(comment)
                .where(comment.parent.id.in(rootCommentIds))
                .groupBy(comment.parent.id)
                .fetch();

        Map<Long, Long> map = new HashMap<>();
        for (Tuple tuple : tuples) {
            Long rootId = tuple.get(0, Long.class);
            Long cnt = tuple.get(1, Long.class);
            map.put(rootId, cnt);
        }
        return map;
    }

    /**
     * 대댓글 API 진입 전, 경로 변수 조합이 유효한지 한 방에 검사합니다.
     *
     * <pre>
     * SELECT 1
     * FROM comments c
     * WHERE c.id = :rootCommentId
     *   AND c.post_id = :postId
     *   AND c.parent_id IS NULL;
     * </pre>
     *
     * <p>대댓글 id를 넣거나, 다른 게시글에 달린 댓글 id를 넣으면 행이 없으므로 {@code false} 입니다.
     */
    @Override
    public boolean existsRootCommentForReplies(Long postId, Long rootCommentId) {
        if (rootCommentId == null) {
            return false;
        }
        Comment one = queryFactory
                .selectFrom(comment)
                .where(
                        comment.id.eq(rootCommentId),
                        comment.post.id.eq(postId),
                        comment.parent.isNull()
                )
                .fetchFirst();
        return one != null;
    }

    /**
     * 특정 원댓글({@code rootCommentId})에 매달린 대댓글만 조회합니다.
     *
     * <p>조건: {@code post_id = postId} 이고 {@code parent_id = rootCommentId}.
     * (같은 테이블에 원댓·대댓이 같이 있으므로, 반드시 게시글 id까지 넣어 다른 글의 동일 parent id 충돌을 막습니다.)
     *
     * <p>정렬: 원댓글 목록과 동일하게 <strong>대화가 위에서 아래로</strong> 읽히도록 {@code createdAt ASC}, 타이브레이크 {@code id ASC}.
     * Slice: {@code limit = pageSize + 1} 로 다음 페이지 존재 여부를 판별합니다.
     */
    @Override
    public Slice<Comment> findRepliesByRootComment(Long postId, Long rootCommentId, Pageable pageable) {
        List<Comment> content = queryFactory
                .selectFrom(comment)
                .join(comment.writer).fetchJoin()
                .where(
                        comment.post.id.eq(postId),
                        comment.parent.id.eq(rootCommentId)
                )
                .orderBy(comment.createdAt.asc(), comment.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        return toSlice(content, pageable);
    }

    private static Slice<Comment> toSlice(List<Comment> items, Pageable pageable) {
        boolean hasNext = items.size() > pageable.getPageSize();
        if (hasNext) {
            items = items.subList(0, pageable.getPageSize());
        }
        return new SliceImpl<>(items, pageable, hasNext);
    }
}
