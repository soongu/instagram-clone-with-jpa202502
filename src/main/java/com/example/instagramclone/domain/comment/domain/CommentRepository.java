package com.example.instagramclone.domain.comment.domain;

import com.example.instagramclone.domain.comment.infrastructure.CommentRepositoryCustom;
import com.example.instagramclone.domain.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Comment 엔티티 JPA 리포지토리 + QueryDSL 커스텀.
 *
 * <p>복잡 조회(원댓글 Slice, 대댓글 Slice, replyCount 배치 집계)는 {@link CommentRepositoryCustom}에 둡니다.
 * (FollowRepository + FollowRepositoryCustom 패턴과 동일)
 */
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    // TODO Day 14: 필요 시 exists / count 메서드 시그니처 추가 (예: 특정 원댓글의 대댓글 수)
}
