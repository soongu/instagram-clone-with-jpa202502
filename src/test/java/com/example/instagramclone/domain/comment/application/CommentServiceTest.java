package com.example.instagramclone.domain.comment.application;

import com.example.instagramclone.core.exception.CommentErrorCode;
import com.example.instagramclone.core.exception.CommentException;
import com.example.instagramclone.core.exception.MemberErrorCode;
import com.example.instagramclone.core.exception.MemberException;
import com.example.instagramclone.core.exception.PostErrorCode;
import com.example.instagramclone.core.exception.PostException;
import com.example.instagramclone.domain.comment.api.CommentCreateRequest;
import com.example.instagramclone.domain.comment.api.CommentResponse;
import com.example.instagramclone.domain.comment.domain.Comment;
import com.example.instagramclone.domain.comment.domain.CommentRepository;
import com.example.instagramclone.domain.member.application.MemberService;
import com.example.instagramclone.domain.member.domain.Member;
import com.example.instagramclone.domain.post.application.PostService;
import com.example.instagramclone.domain.post.domain.Post;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/**
 * {@link CommentService#createComment} 단위 테스트 (Day 14 Step 2).
 *
 * <p>검증 시나리오:
 * <ul>
 *   <li>게시글 없음 → {@link PostException}(POST_NOT_FOUND)</li>
 *   <li>회원 없음 → {@link MemberException}(MEMBER_NOT_FOUND) — MemberService 위임</li>
 *   <li>원댓글 작성 성공 — parentId null</li>
 *   <li>대댓글 작성 성공 — 부모는 원댓글·같은 게시글</li>
 *   <li>부모 id 없음 → COMMENT_NOT_FOUND</li>
 *   <li>부모가 다른 게시글 → INVALID_POST_FOR_COMMENT</li>
 *   <li>부모가 대댓글(이미 parent 있음) → PARENT_NOT_ROOT_COMMENT</li>
 * </ul>
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private CommentService commentService;

    private static final Long POST_ID = 10L;
    private static final Long MEMBER_ID = 1L;

    private Member buildMember(Long id, String username) {
        Member member = Member.builder()
                .username(username)
                .password("pw")
                .email(username + "@t.com")
                .name("이름")
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        ReflectionTestUtils.setField(member, "profileImageUrl", "https://img/" + id + ".png");
        return member;
    }

    private Post buildPost(Long id, Member writer) {
        Post post = Post.builder()
                .content("본문")
                .writer(writer)
                .build();
        ReflectionTestUtils.setField(post, "id", id);
        return post;
    }

    /** 저장 시 id·createdAt을 채워 JPA persist와 유사하게 동작하도록 함 */
    private void stubSaveAssignsIdAndCreatedAt(long newId) {
        given(commentRepository.save(any(Comment.class))).willAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            ReflectionTestUtils.setField(c, "id", newId);
            ReflectionTestUtils.setField(c, "createdAt", LocalDateTime.of(2025, 3, 22, 12, 0, 0));
            return c;
        });
    }

    @Nested
    @DisplayName("createComment()")
    class CreateComment {

        @Test
        @DisplayName("실패 - 게시글이 없으면 PostException(POST_NOT_FOUND) — PostService.getPostByIdOrThrow")
        void fail_when_post_missing() {
            given(postService.getPostByIdOrThrow(POST_ID))
                    .willThrow(new PostException(PostErrorCode.POST_NOT_FOUND));

            CommentCreateRequest req = new CommentCreateRequest("안녕", null);

            assertThatThrownBy(() -> commentService.createComment(POST_ID, req, MEMBER_ID))
                    .isInstanceOf(PostException.class)
                    .hasMessage(PostErrorCode.POST_NOT_FOUND.getMessage());

            then(memberService).shouldHaveNoInteractions();
            then(commentRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 로그인 회원이 없으면 MemberException(MEMBER_NOT_FOUND) — MemberService.findById")
        void fail_when_member_missing() {
            Member writer = buildMember(MEMBER_ID, "u1");
            Post post = buildPost(POST_ID, writer);
            given(postService.getPostByIdOrThrow(POST_ID)).willReturn(post);
            given(memberService.findById(MEMBER_ID))
                    .willThrow(new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

            CommentCreateRequest req = new CommentCreateRequest("안녕", null);

            assertThatThrownBy(() -> commentService.createComment(POST_ID, req, MEMBER_ID))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberErrorCode.MEMBER_NOT_FOUND.getMessage());

            then(commentRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("성공 - 원댓글(parentId null): Comment.save 후 응답 replyCount=0")
        void success_root_comment() {
            Member writer = buildMember(MEMBER_ID, "writer");
            Post post = buildPost(POST_ID, writer);
            given(postService.getPostByIdOrThrow(POST_ID)).willReturn(post);
            given(memberService.findById(MEMBER_ID)).willReturn(writer);
            stubSaveAssignsIdAndCreatedAt(100L);

            CommentCreateRequest req = new CommentCreateRequest("첫 댓글입니다", null);

            CommentResponse response = commentService.createComment(POST_ID, req, MEMBER_ID);

            assertThat(response.id()).isEqualTo(100L);
            assertThat(response.content()).isEqualTo("첫 댓글입니다");
            assertThat(response.memberId()).isEqualTo(MEMBER_ID);
            assertThat(response.username()).isEqualTo("writer");
            assertThat(response.replyCount()).isZero();
            assertThat(response.createdAt()).isNotNull();

            ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
            then(commentRepository).should().save(captor.capture());
            Comment saved = captor.getValue();
            assertThat(saved.getParent()).isNull();
            assertThat(saved.getPost().getId()).isEqualTo(POST_ID);
        }

        @Test
        @DisplayName("성공 - 대댓글: 부모가 같은 글의 원댓글이면 저장")
        void success_reply_to_root() {
            Member writer = buildMember(MEMBER_ID, "writer");
            Member other = buildMember(2L, "rootAuthor");
            Post post = buildPost(POST_ID, other);

            Comment root = Comment.create(post, other, "원댓글", null);
            ReflectionTestUtils.setField(root, "id", 20L);

            given(postService.getPostByIdOrThrow(POST_ID)).willReturn(post);
            given(memberService.findById(MEMBER_ID)).willReturn(writer);
            given(commentRepository.findById(20L)).willReturn(Optional.of(root));
            stubSaveAssignsIdAndCreatedAt(21L);

            CommentCreateRequest req = new CommentCreateRequest("대댓글", 20L);

            CommentResponse response = commentService.createComment(POST_ID, req, MEMBER_ID);

            assertThat(response.replyCount()).isNull();

            ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
            then(commentRepository).should().save(captor.capture());
            assertThat(captor.getValue().getParent().getId()).isEqualTo(20L);
        }

        @Test
        @DisplayName("실패 - parentId에 해당 댓글이 없으면 COMMENT_NOT_FOUND")
        void fail_parent_not_found() {
            Member writer = buildMember(MEMBER_ID, "writer");
            Post post = buildPost(POST_ID, writer);
            given(postService.getPostByIdOrThrow(POST_ID)).willReturn(post);
            given(memberService.findById(MEMBER_ID)).willReturn(writer);
            given(commentRepository.findById(999L)).willReturn(Optional.empty());

            CommentCreateRequest req = new CommentCreateRequest("대댓글", 999L);

            assertThatThrownBy(() -> commentService.createComment(POST_ID, req, MEMBER_ID))
                    .isInstanceOf(CommentException.class)
                    .hasMessage(CommentErrorCode.COMMENT_NOT_FOUND.getMessage());

            then(commentRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패 - 부모 댓글이 다른 게시글에 속하면 INVALID_POST_FOR_COMMENT")
        void fail_parent_on_different_post() {
            Member writer = buildMember(MEMBER_ID, "writer");
            Post post = buildPost(POST_ID, writer);
            Post otherPost = buildPost(99L, writer);
            Comment parentOnOtherPost = Comment.create(otherPost, writer, "다른 글 댓글", null);
            ReflectionTestUtils.setField(parentOnOtherPost, "id", 30L);

            given(postService.getPostByIdOrThrow(POST_ID)).willReturn(post);
            given(memberService.findById(MEMBER_ID)).willReturn(writer);
            given(commentRepository.findById(30L)).willReturn(Optional.of(parentOnOtherPost));

            CommentCreateRequest req = new CommentCreateRequest("잘못된 대댓글", 30L);

            assertThatThrownBy(() -> commentService.createComment(POST_ID, req, MEMBER_ID))
                    .isInstanceOf(CommentException.class)
                    .hasMessage(CommentErrorCode.INVALID_POST_FOR_COMMENT.getMessage());

            then(commentRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("실패 - 부모가 대댓글이면(이미 parent가 있음) PARENT_NOT_ROOT_COMMENT")
        void fail_parent_is_not_root() {
            Member writer = buildMember(MEMBER_ID, "writer");
            Post post = buildPost(POST_ID, writer);
            Comment root = Comment.create(post, writer, "원", null);
            ReflectionTestUtils.setField(root, "id", 40L);
            Comment reply = Comment.create(post, writer, "대1", root);
            ReflectionTestUtils.setField(reply, "id", 41L);

            given(postService.getPostByIdOrThrow(POST_ID)).willReturn(post);
            given(memberService.findById(MEMBER_ID)).willReturn(writer);
            given(commentRepository.findById(41L)).willReturn(Optional.of(reply));

            CommentCreateRequest req = new CommentCreateRequest("대댓글에 답글 시도", 41L);

            assertThatThrownBy(() -> commentService.createComment(POST_ID, req, MEMBER_ID))
                    .isInstanceOf(CommentException.class)
                    .hasMessage(CommentErrorCode.PARENT_NOT_ROOT_COMMENT.getMessage());

            then(commentRepository).should(never()).save(any());
        }
    }
}
