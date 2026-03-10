package com.example.instagramclone.domain.post.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// TODO: 1. JpaRepository를 상속받는 인터페이스로 변경하세요
public interface PostRepository extends JpaRepository<Post, Long> {

    // [과제 2 예시답안] 단방향 매핑 적용 시
    // - ToOne 관계인 writer만 Fetch Join으로 유지하여 1차 쿼리를 날립니다.
    // - (ToMany 컬렉션이 없으므로 default_batch_fetch_size 옵션도 더 이상 필요 없습니다.)
    @Query("""
        SELECT p FROM Post p 
        JOIN FETCH p.writer        
                """)
    Slice<Post> findAllWithImages(Pageable pageable);
}
