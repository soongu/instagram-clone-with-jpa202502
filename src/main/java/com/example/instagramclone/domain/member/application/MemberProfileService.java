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
     * - targetMember: 실제 존재 여부를 검증해야 하므로 findByUsername()
     * - isFollowing 계산은 follow 도메인 서비스에 위임
     * - isCurrentUser 값을 함께 내려줘 클라이언트가 "내 프로필인지" 즉시 판단할 수 있게 한다.
     */
    public MemberProfileResponse getProfileByUsername(Long loginMemberId, String username) {
        // 1. 프로필 주인(타겟)의 정보를 DB에서 가져옵니다. (진짜 존재하는지 검증 필요)
        Member targetMember = memberService.findByUsername(username);
		    
        // 2. 이 프로필이 '나'의 프로필인지 즉시 계산합니다.
        boolean isCurrentUser = loginMemberId.equals(targetMember.getId());
        
        // 3. 로그인 유저는 ID를 이미 신뢰할 수 있으므로 DB 조회 없이 가짜 객체(프록시)만 가져와 성능을 아낍니다.
        Member loginMember = memberService.getReferenceById(loginMemberId);
        
        // 4. "로그인 유저가 이 사람을 팔로우 중인가?" -> FollowService에게 두 엔티티를 넘겨 물어봅니다!
        boolean isFollowing = followService.isFollowing(loginMember, targetMember);

        // 5. 완성된 DTO를 반환합니다.
        return MemberProfileResponse.of(targetMember, isFollowing, isCurrentUser);
    }
}
