package com.example.instagramclone.repository.custom;

import com.example.instagramclone.domain.follow.entity.Follow;

import java.util.List;

public interface FollowRepositoryCustom {
    /**
     * 특정 유저의 팔로워 수 / 팔로잉 수 조회
     * @param userId - 현재 팔로잉 / 팔로워 수를 구하려는 회원의 ID
     * @param type - 현재 구하려는 정보가 팔로잉 수인지 팔로워 수인지 구분
     * @return - 해당 타입의 숫자
     */
    long countFollowByType(Long userId, String type);

    // 특정 유저의 팔로워/팔로잉 유저 목록 조회
    List<Follow> findFollowList(Long userId, String type);

    // 특정 사용자를 팔로우하는 사람들 중
    // 현재 사용자가 팔로우하는 사람들 목록 조회
    List<String> findCommonFollowingUsernames(Long targetUserId, Long currentUserId);
}
