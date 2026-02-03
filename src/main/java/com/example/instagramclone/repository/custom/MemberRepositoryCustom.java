package com.example.instagramclone.repository.custom;

import com.example.instagramclone.domain.member.dto.response.MemberWithStatsDto;
import com.example.instagramclone.domain.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepositoryCustom {

    // 추천할 사용자 목록 조회 (팔로우하지 않은 사용자 중)
    List<Member> findMembersToSuggest(Long currentUserId, int limit);

    // 검색어 기반 회원 검색
    List<Member> searchMembers(String keyword);

    // 프로필 사진 수정
    void updateProfileImage(String imageUrl, String username);

    // 사용자 정보와 통계 조회 (프로필 페이지용)
    Optional<MemberWithStatsDto> findMemberWithStats(String targetUsername, String loginUsername);
}
