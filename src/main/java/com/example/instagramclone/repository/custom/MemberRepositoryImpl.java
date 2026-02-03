package com.example.instagramclone.repository.custom;

import com.example.instagramclone.domain.member.dto.response.MemberWithStatsDto;
import com.example.instagramclone.domain.member.entity.Member;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.instagramclone.domain.follow.entity.QFollow.follow;
import static com.example.instagramclone.domain.member.entity.QMember.member;
import static com.example.instagramclone.domain.post.entity.QPost.post;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final int SEARCH_RESULT_LIMIT = 5;

    @Override
    public List<Member> findMembersToSuggest(Long currentUserId, int limit) {
        NumberTemplate<Double> rand = Expressions.numberTemplate(Double.class, "function('rand')");

        return queryFactory
                .selectFrom(member)
                .where(isNotSelfOrFollowing(currentUserId))
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

    private BooleanExpression isNotSelfOrFollowing(Long currentUserId) {
        return member.id.ne(currentUserId)
                .and(member.id.notIn(
                        JPAExpressions
                                .select(follow.toMember.id)
                                .from(follow)
                                .where(follow.fromMember.id.eq(currentUserId))
                ));
    }

    @Override
    public List<Member> searchMembers(String keyword) {
        return queryFactory
                .selectFrom(member)
                .where(member.username.containsIgnoreCase(keyword))
                .orderBy(member.username.asc())
                .limit(SEARCH_RESULT_LIMIT)
                .fetch();
    }

    @Override
    public void updateProfileImage(String imageUrl, String username) {
        queryFactory
                .update(member)
                .set(member.profileImageUrl, imageUrl)
                .set(member.updatedAt, LocalDateTime.now())
                .where(member.username.eq(username))
                .execute();
    }

    @Override
    public Optional<MemberWithStatsDto> findMemberWithStats(String targetUsername, String loginUsername) {

        return Optional.ofNullable(queryFactory
                .select(Projections.constructor(MemberWithStatsDto.class,
                        member,
                        JPAExpressions.select(post.count()).from(post).where(post.member.eq(member)),
                        JPAExpressions.select(follow.count()).from(follow).where(follow.toMember.eq(member)),
                        JPAExpressions.select(follow.count()).from(follow).where(follow.fromMember.eq(member)),
                        JPAExpressions.selectOne().from(follow)
                                .where(follow.fromMember.username.eq(loginUsername)
                                        .and(follow.toMember.username.eq(targetUsername)))
                                .exists()
                ))
                .from(member)
                .where(member.username.eq(targetUsername))
                .fetchOne());
    }
}