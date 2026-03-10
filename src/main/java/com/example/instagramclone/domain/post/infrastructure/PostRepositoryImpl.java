package com.example.instagramclone.domain.post.infrastructure;

import com.example.instagramclone.domain.post.domain.Post;
import com.example.instagramclone.domain.post.domain.PostRepositoryCustom;
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
 * [🚨 네이밍 컨벤션 필수 준수!]
 * → PostRepository + Impl = "PostRepositoryImpl"
 */
@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused") // queryFactory는 TODO 구현 후 사용됩니다
public class PostRepositoryImpl implements PostRepositoryCustom {

//    private final JPAQueryFactory queryFactory;

    // TODO: 1. QPost를 정적 임포트하고 findAllWithImages 메서드를 QueryDSL로 구현하세요.
    //
    //         [JPQL → QueryDSL 변환 가이드]
    //
    //         기존 JPQL:
    //           "SELECT p FROM Post p JOIN FETCH p.writer"
    //
    //         QueryDSL 변환:
    //           QPost post = QPost.post;
    //
    //           List<Post> posts = queryFactory
    //               .selectFrom(post)
    //               .join(post.writer).fetchJoin()   ← JPQL의 JOIN FETCH
    //               .orderBy(post.id.desc())         ← 최신순 정렬
    //               .offset(pageable.getOffset())    ← 페이지 시작 위치
    //               .limit(pageable.getPageSize() + 1) ← +1 trick: hasNext 판별용
    //               .fetch();
    //
    //         [Slice를 만드는 "+1 트릭"]
    //         QueryDSL은 Slice를 직접 반환하지 않습니다.
    //         대신 요청 사이즈보다 1개 더 조회하여 "다음 페이지 존재 여부"를 판단합니다.
    //
    //           boolean hasNext = posts.size() > pageable.getPageSize();
    //           if (hasNext) {
    //               posts.remove(posts.size() - 1); // 초과분 제거
    //           }
    //           return new SliceImpl<>(posts, pageable, hasNext);
    @Override
    public Slice<Post> findAllWithImages(Pageable pageable) {
        // TODO: 위 힌트를 참고하여 구현하세요.
        return new SliceImpl<>(List.of(), pageable, false);
    }
}
