package com.example.instagramclone.repository;

import com.example.instagramclone.domain.post.entity.Post;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO: 1. JpaRepository를 상속받는 인터페이스로 변경하세요
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // TODO: [Day 7] Fetch Join을 사용하여 N+1 문제 해결하기
    List<Post> findAllWithImages();

    // TODO: [Day 7] 무한 스크롤을 위한 Slice 페이징 추가하기
    Slice<Post> findAllWithImages(Pageable pageable);
}
