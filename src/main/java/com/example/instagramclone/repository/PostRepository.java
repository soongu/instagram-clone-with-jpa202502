package com.example.instagramclone.repository;

import com.example.instagramclone.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO: 1. JpaRepository를 상속받는 인터페이스로 변경하세요
public interface PostRepository extends JpaRepository<Post, Long> {
}
