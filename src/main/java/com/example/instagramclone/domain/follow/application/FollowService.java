package com.example.instagramclone.domain.follow.application;

import com.example.instagramclone.domain.follow.api.FollowListResponse;
import com.example.instagramclone.domain.follow.api.FollowStatusResponse;
import com.example.instagramclone.domain.follow.domain.FollowRepository;
import com.example.instagramclone.domain.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberService memberService;

    @Transactional
    public FollowStatusResponse follow(Long loginMemberId, Long targetMemberId) {
        // TODO Day 13 Step 2
        // 1) loginMember / targetMember 조회
        // 2) 자기 자신 팔로우인지 검사
        // 3) 이미 팔로우 중인지 검사
        // 4) Follow.create(...) 후 저장
        // 5) countByToMember(...) 로 targetMember의 팔로워 수를 다시 계산
        // 6) memberId + following(true) + followerCount 응답 반환
        return FollowStatusResponse.of(targetMemberId, false, 0L);
    }

    @Transactional
    public FollowStatusResponse unfollow(Long loginMemberId, Long targetMemberId) {
        // TODO Day 13 Step 2
        // 1) loginMember / targetMember 조회
        // 2) 팔로우 관계 존재 여부 확인
        // 3) deleteByFromMemberAndToMember(...) 호출
        // 4) countByToMember(...) 로 targetMember의 팔로워 수를 다시 계산
        // 5) memberId + following(false) + followerCount 응답 반환
        return FollowStatusResponse.of(targetMemberId, false, 0L);
    }

    public FollowListResponse getFollowers(Long loginMemberId, Long memberId) {
        // TODO Day 13 Step 4
        // 1) 특정 유저를 팔로우하는 Follow 목록 조회 (toMember 기준)
        // 2) 리스트에 들어갈 Member 엔티티 목록 추출
        // 3) 로그인 유저(Member)가 그 사람들(List<Member>)을 팔로우 중인지 한 번에 조회
        // 4) FollowMemberResponse(memberId, username, name, profileImageUrl, isFollowing, isMe) 로 변환
        return FollowListResponse.empty();
    }

    public FollowListResponse getFollowings(Long loginMemberId, Long memberId) {
        // TODO Day 13 Step 4
        // 1) 특정 유저가 팔로우하고 있는 Follow 목록 조회 (fromMember 기준)
        // 2) 리스트에 들어갈 Member 엔티티 목록 추출
        // 3) 로그인 유저(Member) 기준 isFollowing / isMe 계산
        // 4) followers API 와 방향이 왜 반대인지 학생들과 함께 비교
        return FollowListResponse.empty();
    }
}
