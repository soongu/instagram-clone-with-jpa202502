package com.example.instagramclone.repository;

import com.example.instagramclone.domain.post.entity.Post;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PostRepository {

    // TODO: JPA Repository로 전환하세요
    public void save(Post post) {
    }

    public List<Post> findAll() {
        return List.of();
    }
}
