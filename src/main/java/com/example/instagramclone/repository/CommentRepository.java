package com.example.instagramclone.repository;

import com.example.instagramclone.domain.comment.entity.Comment;
import com.example.instagramclone.repository.custom.CommentRepositoryCustom;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

//@Mapper
public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    // 댓글 생성
//    void insert(Comment comment);

    // 특정 피드 게시물에 달린 댓글 목록 조회 - querydsl 사용
//    List<Comment> findByPostId(Long postId);

    // 단일 댓글 조회 (댓글 작성시 실시간렌더링)
//    Optional<Comment> findById(Long id);

    // 특정 피드에 달린 댓글 수 조회
    long countByPostId(Long postId);

    // Batch: 게시물별 댓글 수 조회
    @Query("SELECT c.post.id, COUNT(c) FROM Comment c WHERE c.post.id IN :postIds GROUP BY c.post.id")
    List<Object[]> countCommentsByPostIdIn(@Param("postIds") List<Long> postIds);

}
