package com.example.instagramclone.repository.custom;

import com.example.instagramclone.domain.follow.entity.Follow;
import com.example.instagramclone.domain.follow.entity.QFollow;
import com.example.instagramclone.domain.member.entity.QMember;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countFollowByType(Long userId, String type) {
        QFollow follow = QFollow.follow;

        BooleanExpression condition = type.equals("follower")
                ? follow.follower.id.eq(userId)
                : follow.following.id.eq(userId);

        Long count = queryFactory
                .select(follow.count())
                .from(follow)
                .where(condition)
                .fetchOne();

        return count != null ? count : 0L;
    }

    @Override
    public List<Follow> findFollowList(Long userId, String type) {
        QFollow follow = QFollow.follow;
        QMember follower = new QMember("follower");
        QMember following = new QMember("following");

        BooleanExpression condition = type.equals("follower")
                ? follow.follower.id.eq(userId)
                : follow.following.id.eq(userId);

        return queryFactory
                .selectFrom(follow)
                .join(follow.follower, follower).fetchJoin()
                .join(follow.following, following).fetchJoin()
                .where(condition)
                .orderBy(follow.createdAt.desc())
                .fetch();
    }

    @Override
    public List<String> findCommonFollowingUsernames(Long targetUserId, Long currentUserId) {
        // 1. 팔로우 테이블 2번 조인 (targetUser의 팔로워들 + currentUser의 팔로워들)
        QMember member = QMember.member;
        QFollow followTarget = new QFollow("followTarget");
        QFollow followCurrent = new QFollow("followCurrent");

        return queryFactory
                .select(member.username)
                .from(member)
                // targetUser의 팔로워들 inner join
                .innerJoin(followTarget)
                .on(
                        followTarget.follower.eq(member),
                        followTarget.following.id.eq(targetUserId)
                )
                // currentUser의 팔로워들 inner join
                .innerJoin(followCurrent)
                .on(
                        followCurrent.follower.eq(member),
                        followCurrent.following.id.eq(currentUserId)
                )
                .fetch();
    }
}
