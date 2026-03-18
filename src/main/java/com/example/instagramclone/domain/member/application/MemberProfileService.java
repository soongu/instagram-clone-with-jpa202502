package com.example.instagramclone.domain.member.application;

import com.example.instagramclone.domain.follow.application.FollowService;
import com.example.instagramclone.domain.member.api.MemberProfileResponse;
import com.example.instagramclone.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프로필 조회 전용 서비스.
 *
 * 회원 기본 정보는 member 도메인에서 가져오고,
 * "로그인 유저가 이 사람을 팔로우 중인가?" 라는 상태값은 follow 도메인의 서비스에 위임해 가져와
 * 프로필 화면에 필요한 응답 DTO로 조립한다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberProfileService {

    private final MemberService memberService;
    private final FollowService followService;

    /**
     * 특정 회원의 프로필 1건 조회.
     *
     * - targetMember: 실제 존재 여부를 검증해야 하므로 findById()
     * - isFollowing 계산은 follow 도메인 서비스에 위임
     * - isCurrentUser 값을 함께 내려줘 클라이언트가 "내 프로필인지" 즉시 판단할 수 있게 한다.
     */
    public MemberProfileResponse getProfile(Long loginMemberId, Long memberId) {
        boolean isCurrentUser = loginMemberId.equals(memberId);
        
        Member targetMember = memberService.findById(memberId);
        Member loginMember = memberService.getReferenceById(loginMemberId);

        boolean isFollowing = followService.isFollowing(loginMember, targetMember);
        
        return MemberProfileResponse.of(targetMember, isFollowing, isCurrentUser);
    }
}
