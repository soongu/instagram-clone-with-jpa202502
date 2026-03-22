package com.example.instagramclone.domain.comment.application;

import com.example.instagramclone.core.common.dto.SliceResponse;
import com.example.instagramclone.core.exception.CommentErrorCode;
import com.example.instagramclone.core.exception.CommentException;
import com.example.instagramclone.domain.comment.api.CommentCreateRequest;
import com.example.instagramclone.domain.comment.api.CommentResponse;
import com.example.instagramclone.domain.comment.domain.Comment;
import com.example.instagramclone.domain.comment.domain.CommentRepository;
import com.example.instagramclone.domain.member.application.MemberService;
import com.example.instagramclone.domain.member.domain.Member;
import com.example.instagramclone.domain.post.application.PostService;
import com.example.instagramclone.domain.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * 댓글 유스케이스 (작성·조회).
 *
 * <p>게시글 존재 여부는 {@link PostService}에 위임하여 <strong>PostRepository를 이 클래스에서 직접 참조하지 않는다.</strong>
 * (도메인 경계: 댓글 애플리케이션은 Post 애플리케이션 서비스만 의존)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    /** 게시글 조회·검증은 Post 도메인 서비스로만 위임 (리포지토리 직접 주입 금지) */
    private final PostService postService;
    /** 댓글 작성자 Member 조회 — 회원 도메인의 공개 조회 API 재사용 */
    private final MemberService memberService;

    /**
     * 댓글 또는 대댓글을 저장합니다.
     *
     * <ol>
     *   <li>게시글({@code postId})이 존재해야 합니다.</li>
     *   <li>작성자는 로그인한 회원({@code loginMemberId})입니다.</li>
     *   <li>{@code parentId}가 없으면 <strong>원댓글</strong>, 있으면 그 id를 부모로 하는 <strong>대댓글</strong>입니다.</li>
     *   <li>대댓글인 경우: 부모 댓글이 반드시 존재하고, 같은 게시글에 속하며, 부모는 <strong>원댓글</strong>({@code parent.parent == null})이어야 합니다.
     *       그렇지 않으면 3-depth 이상이 되어 인스타그램식 2-depth 정책에 어긋납니다.</li>
     * </ol>
     */
    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest request, Long loginMemberId) {
        // 1) Post 도메인: 해당 id의 게시글이 없으면 PostException(POST_NOT_FOUND) — 여기서는 엔티티만 필요
        Post post = postService.getPostByIdOrThrow(postId);

        // 2) Member 도메인: 토큰을 신뢰하고 프록시 참조 사용 (엔티티 전체 조회 대신)
        Member writer = memberService.getReferenceById(loginMemberId);

        // 3) 대댓글이면 부모 댓글 검증(존재·동일 게시글·부모는 원댓글만). 원댓글이면 parent == null
        Comment parent = resolveParentOrThrow(postId, request.parentId());

        // 4) 도메인 팩토리로 엔티티 생성 후 저장
        Comment comment = Comment.create(post, writer, request.content(), parent);
        Comment saved = commentRepository.save(comment);

        // 5) API 응답 DTO (원댓글은 replyCount=0, 대댓글은 null 등 규칙은 CommentResponse.from 참고)
        return CommentResponse.from(saved);
    }

    /**
     * {@code parentId}가 null이면 원댓글이므로 부모 없음.
     * 값이 있으면 부모 댓글을 조회하고, 게시글 일치 및 2-depth(부모는 원댓글만)를 검증합니다.
     */
    private Comment resolveParentOrThrow(Long postId, Long parentId) {
        // 원댓글이면 early return
        if (parentId == null) {
            return null;
        }

        // 부모 id에 해당하는 원댓글이 없으면 잘못된 요청
        Comment parent = commentRepository.findById(parentId)
                .orElseThrow(() -> new CommentException(CommentErrorCode.COMMENT_NOT_FOUND));

        // 부모 댓글이 다른 게시글에 달린 것이면 경로(postId)와 본문이 맞지 않음
        if (!parent.getPost().getId().equals(postId)) {
            throw new CommentException(CommentErrorCode.INVALID_POST_FOR_COMMENT);
        }

        // 부모가 이미 "대댓글"이면 여기에 또 달면 3-depth → 정책 위반
        if (parent.getParent() != null) {
            throw new CommentException(CommentErrorCode.PARENT_NOT_ROOT_COMMENT);
        }

        return parent;
    }

    /**
     * 게시글의 원댓글 목록 (replyCount 포함).
     *
     * @param loginMemberId 향후 "내가 쓴 댓글" 표시 등에 사용
     */
    public SliceResponse<CommentResponse> getRootComments(Long postId, Pageable pageable, Long loginMemberId) {
        // TODO Day 14: postService.getPostByIdOrThrow(postId) 로 존재 검증 → findRootCommentsByPostId → replyCount 배치 집계 → CommentResponse 조립
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
