package com.example.instagramclone.repository.custom;

import com.example.instagramclone.domain.follow.entity.QFollow;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.domain.member.entity.QMember;
import com.example.instagramclone.domain.post.entity.QPost;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findMembersToSuggest(Long currentUserId, int limit) {
        QMember member = QMember.member;
        QFollow follow = QFollow.follow;
        QPost post = QPost.post;

        NumberTemplate<Double> rand = Expressions.numberTemplate(Double.class, "function('rand')");

        return queryFactory
                .selectFrom(member)
                .where(
                        member.id.ne(currentUserId),
                        member.id.notIn(
                                JPAExpressions
                                        .select(follow.follower.id)
                                        .from(follow)
                                        .where(follow.following.id.eq(currentUserId))
                        )
                )
                .orderBy(
                        member.createdAt.desc(),
                        new OrderSpecifier<>(Order.DESC,
                                JPAExpressions
                                        .select(post.count())
                                        .from(post)
                                        .where(post.member.eq(member))
                        ),
                        rand.asc()
                )
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Member> searchMembers(String keyword) {
        QMember member = QMember.member;

        return queryFactory
                .selectFrom(member)
                .where(member.username.containsIgnoreCase(keyword))
                .orderBy(member.username.asc())
                .limit(5)
                .fetch();
    }

    @Override
    public void updateProfileImage(String imageUrl, String username) {
        QMember member = QMember.member;

        queryFactory
                .update(member)
                .set(member.profileImageUrl, imageUrl)
                .set(member.updatedAt, LocalDateTime.now())
                .where(member.username.eq(username))
                .execute();
    }
}