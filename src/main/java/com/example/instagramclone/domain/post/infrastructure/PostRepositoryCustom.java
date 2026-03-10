package com.example.instagramclone.domain.post.infrastructure;

import com.example.instagramclone.domain.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * PostRepository에 QueryDSL 기반 커스텀 피드 조회 쿼리를 추가하기 위한 인터페이스입니다.
 *
 * [Step 4 목표]
 * 현재 PostRepository에 @Query JPQL로 작성된 findAllWithImages()를
 * 아래 순서로 QueryDSL로 완전히 대체합니다.
 *
 *   1단계: 이 인터페이스에 같은 메서드 시그니처를 선언한다.
 *   2단계: PostRepositoryImpl에서 QueryDSL로 구현한다.
 *   3단계: PostRepository에서 @Query를 제거하고 이 인터페이스를 extend에 추가한다.
 *         → public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom
 *   4단계: PostService는 변경 없이 그대로 동작한다. (리팩토링의 철칙!)
 */
public interface PostRepositoryCustom {

    // TODO: 1. 아래 JPQL 쿼리를 QueryDSL로 대체할 메서드를 선언하세요.
    //         메서드 시그니처는 PostRepository의 기존 메서드와 동일하게 유지해야
    //         PostService를 수정하지 않아도 됩니다.
    //
    //         [현재 JPQL - PostRepository.java에 있는 것]
    //         @Query("SELECT p FROM Post p JOIN FETCH p.writer")
    //         Slice<Post> findAllWithImages(Pageable pageable);
    //
    //         힌트: Slice<Post> findAllWithImages(Pageable pageable);
    Slice<Post> findAllWithImages(Pageable pageable);
}
