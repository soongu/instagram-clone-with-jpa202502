package com.example.instagramclone.domain.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

// TODO: 1. JpaRepository를 상속받는 인터페이스를 생성하세요 (제네릭: PostImage, Long)
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

    // [과제 2 예시답안] 단방향 매핑에서 최적화된 자식 조회를 위한 IN 쿼리 추가
    List<PostImage> findByPostIn(List<Post> posts);
}
