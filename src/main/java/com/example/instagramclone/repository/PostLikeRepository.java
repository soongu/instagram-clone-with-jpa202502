package com.example.instagramclone.repository;

import com.example.instagramclone.domain.like.entity.PostLike;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

//@Mapper
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    // 좋아요 추가
//    void insert(PostLike postLike);

    // 좋아요 삭제
//    void delete(@Param("postId") Long postId, @Param("memberId") Long memberId);
    void deleteByPostIdAndMemberId(Long postId, Long memberId);

    // 좋아요 여부 확인
    Optional<PostLike> findByPostIdAndMemberId(Long postId, Long memberId);

    // 피드 게시물의 총 좋아요 수 조회
    long countByPostId(Long postId);

    // Batch: 특정 사용자가 좋아요한 게시물 목록 조회
    List<PostLike> findByMemberIdAndPostIdIn(Long memberId, List<Long> postIds);

    // Batch: 게시물별 좋아요 수 조회
    @Query("SELECT pl.post.id, COUNT(pl) FROM PostLike pl WHERE pl.post.id IN :postIds GROUP BY pl.post.id")
    List<Object[]> countLikesByPostIdIn(@Param("postIds") List<Long> postIds);
}
