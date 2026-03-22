package com.example.instagramclone.domain.comment.application;

import com.example.instagramclone.core.common.dto.SliceResponse;
import com.example.instagramclone.core.exception.CommentErrorCode;
import com.example.instagramclone.core.exception.CommentException;
import com.example.instagramclone.domain.comment.api.CommentCreateRequest;
import com.example.instagramclone.domain.comment.api.CommentResponse;
import com.example.instagramclone.domain.comment.domain.CommentRepository;
import com.example.instagramclone.domain.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * 댓글 유스케이스 (작성·조회).
 *
 * <p>Day 14 라이브 코딩에서 {@link CommentRepository} JPA 저장, 2-Depth 검증,
 * {@link CommentRepository} 커스텀 QueryDSL 조회, DTO 조립을 완성합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /**
     * 댓글/대댓글 작성.
     *
     * <p>TODO Day 14: Post 존재 확인, 부모 댓글 검증(같은 post, 원댓글만 부모 허용), {@link com.example.instagramclone.domain.comment.domain.Comment#create} 후 save
     */
    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest request, Long loginMemberId) {
        // TODO Day 14 라이브 코딩: Post/Member 조회, 2-Depth 검증, Comment.create + save, CommentResponse 조립
        return null;
    }

    /**
     * 게시글의 원댓글 목록 (replyCount 포함).
     *
     * @param loginMemberId 향후 "내가 쓴 댓글" 표시 등에 사용
     */
    public SliceResponse<CommentResponse> getRootComments(Long postId, Pageable pageable, Long loginMemberId) {
        // TODO Day 14: post 존재 검증 → findRootCommentsByPostId → replyCount 배치 집계 → CommentResponse 조립
        return SliceResponse.of(false, Collections.emptyList());
    }

    /**
     * 원댓글에 달린 대댓글 목록.
     */
    public SliceResponse<CommentResponse> getReplies(Long postId, Long rootCommentId, Pageable pageable, Long loginMemberId) {
        // TODO Day 14: root가 해당 post의 원댓글인지 검증 → findRepliesByRootComment → DTO 조립 (replyCount null)
        return SliceResponse.of(false, Collections.emptyList());
    }
}
