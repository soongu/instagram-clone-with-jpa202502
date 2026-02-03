package com.example.instagramclone.repository.custom;

import com.example.instagramclone.domain.comment.entity.QComment;
import com.example.instagramclone.domain.follow.entity.QFollow;
import com.example.instagramclone.domain.like.entity.QPostLike;
import com.example.instagramclone.domain.member.entity.QMember;
import com.example.instagramclone.domain.post.dto.response.ProfilePostResponse;
import com.example.instagramclone.domain.post.entity.Post;
import com.example.instagramclone.domain.post.entity.QPost;
import com.example.instagramclone.domain.post.entity.QPostImage;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Slice<Post> findAllWithMemberAndImages(Pageable pageable) {
        QPost post = QPost.post;
        QMember member = QMember.member;
        QPostImage postImage = QPostImage.postImage;

        // 1. 먼저 필요한 post id들만 조회
        List<Long> postIds = queryFactory
                .select(post.id)
                .from(post)
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        if (postIds.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, false);
        }

        // 2. 조회된 id로 실제 데이터를 join해서 조회
        List<Post> posts = queryFactory
                .selectFrom(post)
                .innerJoin(post.member, member).fetchJoin()
                .leftJoin(post.images, postImage).fetchJoin()
                .where(post.id.in(postIds))
                .orderBy(post.createdAt.desc(), postImage.imageOrder.asc())
                .fetch();

        boolean hasNext = false;
        if (postIds.size() > pageable.getPageSize()) {
            posts.remove(posts.size() - 1);
            hasNext = true;
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }

    @Override
    public Optional<Post> findPostDetailById(Long postId) {
        QPost post = QPost.post;
        QMember member = QMember.member;
        QPostImage postImage = QPostImage.postImage;

        Post result = queryFactory
                .selectFrom(post)
                .join(post.member, member).fetchJoin()
                .join(post.images, postImage).fetchJoin()
                .where(post.id.eq(postId))
                .orderBy(postImage.imageOrder.asc())
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Slice<Post> findFeedPosts(Long memberId, Pageable pageable) {
        QPost post = QPost.post;
        QMember member = QMember.member;
        QPostImage postImage = QPostImage.postImage;
        QFollow follow = QFollow.follow;

        // 1. 먼저 필요한 post id들만 조회
        List<Long> postIds = queryFactory
                .select(post.id)
                .from(post)
                .where(post.member.id.in(
                                        JPAExpressions
                                                .select(follow.toMember.id)
                                                .from(follow)
                                                .where(follow.fromMember.id.eq(memberId))
                                )
                                .or(post.member.id.eq(memberId))
                )
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        if (postIds.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, false);
        }

        // 2. 조회된 id로 실제 데이터를 join해서 조회
        List<Post> posts = queryFactory
                .selectFrom(post)
                .join(post.member, member).fetchJoin()
                .leftJoin(post.images, postImage).fetchJoin()
                .where(post.id.in(postIds))
                .orderBy(post.createdAt.desc(), postImage.imageOrder.asc())
                .fetch();

        // 3. 더보기 여부 확인
        boolean hasNext = false;
        if (postIds.size() > pageable.getPageSize()) {
            posts.remove(posts.size() - 1);
            hasNext = true;
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }

    @Override
    public Slice<Post> findRecommendedPosts(Pageable pageable) {
        QPost post = QPost.post;
        QMember member = QMember.member;
        QPostImage postImage = QPostImage.postImage;
        QPostLike postLike = QPostLike.postLike;
        QComment comment = QComment.comment;

        // 1. 먼저 인기도 순으로 post id들을 조회
        List<Long> postIds = queryFactory
                .select(post.id)
                .from(post)
                .leftJoin(postLike).on(post.id.eq(postLike.post.id))
                .leftJoin(comment).on(post.id.eq(comment.post.id))
                .where(post.createdAt.goe(LocalDateTime.now().minusDays(7)))
                .groupBy(post.id, post.createdAt)
                .orderBy(
                        postLike.id.countDistinct().add(comment.id.countDistinct()).desc(),
                        post.createdAt.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        if (postIds.isEmpty()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, false);
        }

        // 2. 조회된 id로 실제 데이터를 join해서 조회
        List<Post> posts = queryFactory
                .selectFrom(post)
                .join(post.member, member).fetchJoin()
                .leftJoin(post.images, postImage).fetchJoin()
                .where(post.id.in(postIds))
                .orderBy(post.createdAt.desc(), postImage.imageOrder.asc())
                .fetch();

        // 3. 더보기 여부 확인
        boolean hasNext = false;
        if (postIds.size() > pageable.getPageSize()) {
            hasNext = true;
            posts.remove(posts.size() - 1);
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }

    @Override
    public List<ProfilePostResponse> findPostsByHashtag(String tagName, int offset, int limit) {
        String sql = """
                SELECT
                    p.id,
                    pi.image_url           AS mainThumbnail,
                    NVL(l.likeCount, 0)    AS likeCount,
                    NVL(c.commentCount, 0) AS commentCount
                FROM posts p
                INNER JOIN post_hashtags ph ON p.id = ph.post_id
                INNER JOIN (
                        SELECT id, name
                        FROM hashtags
                        WHERE name = ?
                ) h ON ph.hashtag_id = h.id
                LEFT OUTER JOIN
                    (SELECT post_id, image_url
                    FROM post_images
                    WHERE image_order = 1) pi ON p.id = pi.post_id
                LEFT OUTER JOIN
                    (SELECT post_id, COUNT(*) AS likeCount
                    FROM post_likes
                    GROUP BY post_id) l ON p.id = l.post_id
                LEFT OUTER JOIN
                    (SELECT post_id, COUNT(*) AS commentCount
                    FROM comments
                    GROUP BY post_id) c ON p.id = c.post_id
                ORDER BY p.created_at DESC
                LIMIT ? OFFSET ?
                """;

        return jdbcTemplate.query(sql, (rs, n) -> ProfilePostResponse.builder()
                .likeCount(rs.getLong("likeCount"))
                .commentCount(rs.getLong("commentCount"))
                .id(rs.getLong("id"))
                .mainThumbnail(rs.getString("mainThumbnail"))
                .build(), tagName, limit, offset);
    }

    @Override
    public List<ProfilePostResponse> findProfilePosts(Long memberId) {
        return jdbcTemplate.query("""
            SELECT
                p.id,
                pi.image_url as mainThumbnail,
                NVL(l.likeCount, 0) AS likeCount,
                NVL(c.commentCount, 0) AS commentCount
            FROM posts p
            INNER JOIN (
                SELECT post_id, image_url
                FROM post_images
                WHERE image_order = 1
            ) pi ON p.id = pi.post_id
            LEFT JOIN (
                SELECT post_id, COUNT(*) AS likeCount
                FROM post_likes
                GROUP BY post_id
            ) l ON p.id = l.post_id
            LEFT JOIN (
                SELECT post_id, COUNT(*) AS commentCount
                FROM comments
                GROUP BY post_id
            ) c ON p.id = c.post_id
            WHERE p.member_id = ?
            ORDER BY p.created_at DESC
           """, (rs, n) -> ProfilePostResponse.builder()
                .likeCount(rs.getLong("likeCount"))
                .commentCount(rs.getLong("commentCount"))
                .id(rs.getLong("id"))
                .mainThumbnail(rs.getString("mainThumbnail"))
                .build(), memberId);
    }

//    @Override
//    public List<ProfilePostResponse> findProfilePosts(Long memberId) {
//        QPost post = QPost.post;
//        QPostImage postImage = QPostImage.postImage;
//
//        // 좋아요 카운트 서브쿼리
//        QPostLike subLike = new QPostLike("subLike");
//        JPQLQuery<Long> likeCounts = JPAExpressions
//                .select(subLike.count())
//                .from(subLike)
//                .where(subLike.post.eq(post))
//                .groupBy(subLike.post.id);
//
//        // 댓글 카운트 서브쿼리
//        QComment subComment = new QComment("subComment");
//        JPQLQuery<Long> commentCounts = JPAExpressions
//                .select(subComment.count())
//                .from(subComment)
//                .where(subComment.post.eq(post))
//                .groupBy(subComment.post.id);
//
//        return queryFactory
//                .select(
//                        Projections.constructor(
//                                ProfilePostResponse.class,
//                                post.id,
//                                postImage.imageUrl,
//                                likeCounts.select(subLike.count().coalesce(0L)),
//                                commentCounts.select(subComment.count().coalesce(0L))
//                        )
//                )
//                .from(post)
//                .innerJoin(postImage)
//                .on(post.id.eq(postImage.post.id), postImage.imageOrder.eq(1))
//                .where(post.member.id.eq(memberId))
//                .orderBy(post.createdAt.desc())
//                .fetch();
//    }
}
