package com.example.instagramclone.domain.post.infrastructure;

import com.example.instagramclone.domain.post.domain.Post;
import com.example.instagramclone.domain.post.domain.QPost;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * PostRepositoryCustom의 QueryDSL 구현체입니다.
 * 기존 @Query JPQL 피드 조회 쿼리를 타입 세이프한 QueryDSL 코드로 대체합니다.
 *
 * [네이밍 컨벤션 필수]
 * Spring Data JPA는 fragment 인터페이스명 + "Impl" 접미사로 구현체를 탐색합니다.
 * PostRepositoryCustom → PostRepositoryCustomImpl (같은 패키지에 위치해야 함)
 *
 * [JPQL → QueryDSL 변환]
 * JPQL:    "SELECT p FROM Post p JOIN FETCH p.writer"
 * QueryDSL: post.writer 를 fetchJoin() 으로 연결 → 컴파일 타임에 오타 검증 가능
 */
@Repository
@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 피드를 최신순으로 페이징 조회합니다.
     * writer를 JOIN FETCH하여 N+1 쿼리를 방지합니다.
     *
     * [+1 트릭으로 Slice hasNext 판별]
     * QueryDSL은 Slice를 직접 반환하지 않으므로, pageSize + 1 만큼 조회한 뒤
     * 실제로 1개 초과분이 존재하면 hasNext = true로 판단하고 초과분을 제거합니다.
     */
    @Override
    public Slice<Post> findAllWithImages(Pageable pageable) {
        QPost post = QPost.post;

        List<Post> posts = queryFactory
                .selectFrom(post)
                .join(post.writer).fetchJoin()
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = posts.size() > pageable.getPageSize();
        if (hasNext) {
            posts.remove(posts.size() - 1);
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }

    /**
     * 특정 회원의 게시글을 최신순으로 페이징 조회합니다.
     *
     * findAllWithImages와의 차이점:
     * - WHERE 조건(post.writer.id.eq(writerId))으로 특정 회원 게시글만 필터링합니다.
     * - 프로필 페이지에서는 작성자 정보가 이미 표시되어 있으므로 writer fetch join이 불필요합니다.
     *   이미지 조회는 서비스 레이어에서 IN 쿼리로 별도 처리합니다.
     */
    @Override
    public Slice<Post> findAllByWriterId(Long writerId, Pageable pageable) {
        QPost post = QPost.post;

        List<Post> posts = queryFactory
                .selectFrom(post)
                .where(post.writer.id.eq(writerId))
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1L)
                .fetch();

        boolean hasNext = posts.size() > pageable.getPageSize();
        if (hasNext) {
            posts.remove(posts.size() - 1);
        }

        return new SliceImpl<>(posts, pageable, hasNext);
    }
}
