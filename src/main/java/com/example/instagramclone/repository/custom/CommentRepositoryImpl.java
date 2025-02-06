package com.example.instagramclone.repository.custom;

import com.example.instagramclone.domain.comment.entity.Comment;
import com.example.instagramclone.domain.comment.entity.QComment;
import com.example.instagramclone.domain.member.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findByPostIdWithMember(Long postId) {
        QComment comment = QComment.comment;
        QMember member = QMember.member;

        return queryFactory
                .selectFrom(comment)
                .join(comment.member, member).fetchJoin()
                .where(comment.post.id.eq(postId))
                .orderBy(comment.createdAt.asc())
                .fetch();
    }
}
