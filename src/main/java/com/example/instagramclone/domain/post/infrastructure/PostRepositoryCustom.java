package com.example.instagramclone.domain.post.infrastructure;

import com.example.instagramclone.domain.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

/**
 * PostRepository에 QueryDSL 기반 커스텀 피드 조회 쿼리를 추가하기 위한 인터페이스입니다.
 *
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

    Slice<Post> findAllWithImages(Pageable pageable);

    /**
     * 특정 회원의 게시글을 최신순으로 페이징 조회합니다.
     * 프로필 페이지 그리드 API에서 사용합니다.
     */
    Slice<Post> findAllByWriterId(Long writerId, Pageable pageable);
}
