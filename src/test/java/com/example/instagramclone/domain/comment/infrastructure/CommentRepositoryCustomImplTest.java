package com.example.instagramclone.domain.comment.infrastructure;

import com.example.instagramclone.domain.comment.domain.Comment;
import com.example.instagramclone.domain.comment.domain.CommentRepository;
import com.example.instagramclone.domain.member.domain.Member;
import com.example.instagramclone.domain.member.domain.MemberRepository;
import com.example.instagramclone.domain.post.domain.Post;
import com.example.instagramclone.domain.post.domain.PostRepository;
import com.example.instagramclone.infrastructure.persistence.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link CommentRepositoryCustomImpl} QueryDSL 쿼리 통합 테스트 (@DataJpaTest + H2).
 *
 * <p>QComment 생성·컴파일이 선행되어야 하며, {@link QueryDslConfig} 로 {@link com.querydsl.jpa.impl.JPAQueryFactory} 를 주입합니다.
 */
@DataJpaTest
@Import(QueryDslConfig.class)
class CommentRepositoryCustomImplTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    private Member writer;
    private Post post;

    @BeforeEach
    void setUp() {
        writer = memberRepository.save(Member.builder()
                .username("comment_writer")
                .password("pw")
                .email("cw@test.com")
                .name("작성자")
                .build());

        post = postRepository.save(Post.builder()
                .content("게시글")
                .writer(writer)
                .build());
    }

    @Test
    @DisplayName("findRootCommentsByPostId: parent가 null인 행만, 시간순(createdAt asc, id asc)")
    void findRootComments_only_parent_null_ordered() {
        // given: 원댓글 2개 + 대댓글 1개(첫 원댓글에 달림)
        Comment rootOld = commentRepository.save(Comment.create(post, writer, "옛날 원댓", null));
        Comment rootNew = commentRepository.save(Comment.create(post, writer, "최신 원댓", null));
        commentRepository.save(Comment.create(post, writer, "대댓", rootOld));

        Pageable pageable = PageRequest.of(0, 10);

        // when
        Slice<Comment> slice = commentRepository.findRootCommentsByPostId(post.getId(), pageable);

        // then: 대댓글은 제외, 2건만·먼저 달린 원댓이 위(시간순)
        assertThat(slice.getContent()).hasSize(2);
        assertThat(slice.getContent().get(0).getId()).isEqualTo(rootOld.getId());
        assertThat(slice.getContent().get(1).getId()).isEqualTo(rootNew.getId());
        assertThat(slice.hasNext()).isFalse();
    }

    @Test
    @DisplayName("countRepliesByRootCommentIds: 원댓글별 대댓글 수, 없으면 맵에 키 없음")
    void countReplies_batch() {
        Comment rootA = commentRepository.save(Comment.create(post, writer, "A", null));
        Comment rootB = commentRepository.save(Comment.create(post, writer, "B", null));
        commentRepository.save(Comment.create(post, writer, "A-대1", rootA));
        commentRepository.save(Comment.create(post, writer, "A-대2", rootA));

        Map<Long, Long> map = commentRepository.countRepliesByRootCommentIds(Set.of(rootA.getId(), rootB.getId()));

        assertThat(map.get(rootA.getId())).isEqualTo(2L);
        assertThat(map.get(rootB.getId())).isNull();
    }
}
