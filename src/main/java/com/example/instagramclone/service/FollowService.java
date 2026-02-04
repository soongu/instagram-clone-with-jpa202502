package com.example.instagramclone.service;

import com.example.instagramclone.domain.follow.dto.response.FollowResponse;
import com.example.instagramclone.domain.follow.dto.response.FollowStatus;
import com.example.instagramclone.domain.follow.dto.response.FollowToggleResponse;
import com.example.instagramclone.domain.follow.entity.Follow;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.exception.ErrorCode;
import com.example.instagramclone.exception.MemberException;
import com.example.instagramclone.repository.FollowRepository;
import com.example.instagramclone.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.instagramclone.domain.follow.dto.response.FollowStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;

    // 팔로우 / 언팔로우 토글
    @Transactional
    public FollowToggleResponse toggleFollow(String followingUserName, String followerUserName) {

        // 자기 자신을 팔로우하는 것을 방지 (Fast-Fail)
        if (followingUserName.equals(followerUserName)) {
            throw new MemberException(ErrorCode.SELF_FOLLOW);
        }

        // 팔로잉한 회원정보와 팔로우당한 회원정보 조회
        Member following = getMember(followingUserName);
        Member follower = getMember(followerUserName);

        Long followerId = follower.getId();
        Long followingId = following.getId();

        // 팔로우 여부 확인 및 처리 (조회 후 존재하면 삭제, 없으면 저장)
        boolean isFollow = followRepository.findByFromMemberIdAndToMemberId(followingId, followerId)
                .map(follow -> {
                    followRepository.delete(follow); // 이미 팔로우 중이면 삭제
                    return true;
                })
                .orElseGet(() -> {
                    followRepository.save( // 팔로우 중이 아니면 저장
                            Follow.builder()
                                    .fromMember(following)
                                    .toMember(follower)
                                    .build()
                    );
                    return false;
                });

        // 팔로우 / 언팔로우 완료 시 응답할 데이터
        return FollowToggleResponse.of(
                !isFollow, // isFollow가 true면 삭제되었으므로 현재 상태는 false(언팔), false면 추가되었으므로 true(팔로우)
                followRepository.countFollowByType(followerId, "follower")
        );
    }


    // 특정 유저의 팔로잉 / 팔로워 목록 조회
    public List<FollowResponse> getFollows(String targetUsername, String loginUsername, FollowStatus type) {

        Member foundMember = getMember(targetUsername);
        Member loginMember = getMember(loginUsername);

        List<Follow> followList;
        if (type == FOLLOWER) {
            followList = followRepository.findFollowList(foundMember.getId(), FOLLOWER.name().toLowerCase());
        } else {
            followList = followRepository.findFollowList(foundMember.getId(), FOLLOWING.name().toLowerCase());
        }

        // 로그인 유저가 팔로우하고 있는 사람들의 ID 목록 조회 (Batch)
        List<Long> targetMemberIds = followList.stream()
                .map(f -> type == FOLLOWER ? f.getFromMember().getId() : f.getToMember().getId())
                .collect(Collectors.toList());

        List<Follow> myFollows = followRepository.findByFromMemberIdAndToMemberIdIn(loginMember.getId(), targetMemberIds);
        Set<Long> myFollowingIds = myFollows.stream()
                .map(f -> f.getToMember().getId())
                .collect(Collectors.toSet());

        return followList.stream()
                .map(follow -> {
                    Member target = type == FOLLOWER ? follow.getFromMember() : follow.getToMember();
                    return FollowResponse.of(
                            follow,
                            myFollowingIds.contains(target.getId()),
                            type
                    );
                })
                .collect(Collectors.toList());
    }


    private Member getMember(String username) {
        return memberRepository.findByUsername(username).orElseThrow(
                () -> new MemberException(ErrorCode.MEMBER_NOT_FOUND)
        );
    }
}
