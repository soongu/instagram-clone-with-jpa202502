package com.example.instagramclone.repository;

import com.example.instagramclone.domain.hashtag.entity.PostHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
}
