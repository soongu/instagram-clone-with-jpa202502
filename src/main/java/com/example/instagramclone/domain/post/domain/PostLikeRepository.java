package com.example.instagramclone.domain.post.domain;

import com.example.instagramclone.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * - existsByMemberAndPost: 토글 시 "이미 좋아요 했는지" 판단
 * - deleteByMemberAndPost: 좋아요 취소 시 삭제
 */
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByMemberAndPost(Member member, Post post);

    void deleteByMemberAndPost(Member member, Post post);
}
