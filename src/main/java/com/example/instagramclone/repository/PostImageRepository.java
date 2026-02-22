package com.example.instagramclone.repository;

import com.example.instagramclone.domain.post.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO: 1. JpaRepository를 상속받는 인터페이스를 생성하세요 (제네릭: PostImage, Long)
public interface PostImageRepository extends JpaRepository<PostImage, Long> {
}
