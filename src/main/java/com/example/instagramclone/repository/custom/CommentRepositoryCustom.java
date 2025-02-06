package com.example.instagramclone.repository.custom;

import com.example.instagramclone.domain.comment.entity.Comment;

import java.util.List;

public interface CommentRepositoryCustom {

    // 특정 피드의 댓글 목록 조회
    List<Comment> findByPostIdWithMember(Long postId);
}
