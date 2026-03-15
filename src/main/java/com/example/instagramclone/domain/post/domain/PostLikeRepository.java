package com.example.instagramclone.domain.post.domain;

import com.example.instagramclone.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * [Day 12 Step 2] 좋아요(PostLike) 조회·삭제용 리포지토리.
 *
 * - existsByMemberAndPost: 토글 시 "이미 좋아요 했는지" 판단
 * - deleteByMemberAndPost: 좋아요 취소 시 레코드 삭제
 * - countByPost: 해당 게시물의 좋아요 개수 (Step 2에서는 COUNT 쿼리로 응답용. Part 2에서 비정규화하면 Post.likeCount로 대체 가능)
 */
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByMemberAndPost(Member member, Post post);

    void deleteByMemberAndPost(Member member, Post post);

    /** 해당 게시물에 달린 좋아요 개수. Step 2에서는 이걸로 응답 likeCount를 채웁니다. */
    long countByPost(Post post);
}
