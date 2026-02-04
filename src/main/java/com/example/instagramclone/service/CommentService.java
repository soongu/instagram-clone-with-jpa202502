package com.example.instagramclone.service;

import com.example.instagramclone.domain.comment.dto.response.CommentCreationResponse;
import com.example.instagramclone.domain.comment.dto.response.CommentResponse;
import com.example.instagramclone.domain.comment.entity.Comment;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.domain.post.entity.Post;
import com.example.instagramclone.exception.ErrorCode;
import com.example.instagramclone.exception.MemberException;
import com.example.instagramclone.exception.PostException;
import com.example.instagramclone.repository.CommentRepository;
import com.example.instagramclone.repository.MemberRepository;
import com.example.instagramclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    // 댓글 작성 처리
    @Transactional
    public CommentCreationResponse createComment(Long postId, String username, String content) {

        Member foundMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        Post foundPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(ErrorCode.POST_NOT_FOUND));

        // 새로 작성할 댓글 엔터티 객체
        Comment newComment = Comment.of(
                foundPost,
                foundMember,
                content
        );

        commentRepository.save(newComment);

        // 댓글 작성 시 응답해야할 데이터
        // 1. 방금 생성된 댓글정보 (이미 메모리에 있으므로 다시 조회할 필요 없음)
        CommentResponse commentResponse = CommentResponse.from(newComment);
        
        // 2. 이 피드에 달린 댓글의 총 개수
        long totalCommentCount = commentRepository.countByPostId(postId);

        return CommentCreationResponse.of(commentResponse, totalCommentCount);
    }


}
