package com.example.instagramclone.domain.follow.application;

import com.example.instagramclone.core.exception.FollowErrorCode;
import com.example.instagramclone.core.exception.FollowException;
import com.example.instagramclone.domain.follow.api.FollowListResponse;
import com.example.instagramclone.domain.follow.api.FollowStatusResponse;
import com.example.instagramclone.domain.follow.domain.Follow;
import com.example.instagramclone.domain.follow.domain.FollowRepository;
import com.example.instagramclone.domain.member.application.MemberService;
import com.example.instagramclone.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FollowService {

    private final FollowRepository followRepository;
    private final MemberService memberService;

    /**
     * 팔로우 API.
     *
     * 흐름:
     * 1) 로그인 유저와 대상 유저를 조회한다.
     * 2) 자기 자신을 팔로우하려는지 검사한다.
     * 3) 이미 팔로우 중인지 검사한다.
     * 4) Follow 엔티티를 생성해 저장한다.
     * 5) 대상 유저의 최신 팔로워 수를 다시 조회해 응답에 담는다.
     *
     * 왜 countByToMember()를 다시 호출할까?
     * - 프론트는 이 응답만으로 버튼 상태뿐 아니라 프로필 상단 팔로워 수도 즉시 갱신하고 싶어 한다.
     * - 그래서 "팔로우 성공 여부"만이 아니라 "현재 팔로워 수"까지 함께 돌려준다.
     *
     * 왜 loginMember는 getReferenceById(), targetMember는 findById()를 사용할까?
     * - loginMemberId는 이미 인증 필터가 검증한 JWT에서 꺼낸 값이므로 비교적 신뢰할 수 있다.
     *   따라서 반복 호출이 많은 팔로우 API에서는 매번 SELECT 하지 않고 프록시로 받아 성능을 아낀다.
     * - 반면 targetMemberId는 클라이언트가 URL로 보내는 값이므로 신뢰할 수 없다.
     *   그래서 실제 존재 여부를 즉시 검증하기 위해 findById()로 조회한다.
     * - 즉, "로그인 유저는 성능 최적화", "대상 유저는 정합성 검증"이라는 절충안이다.
     */
    @Transactional
    public FollowStatusResponse follow(Long loginMemberId, Long targetMemberId) {
        // JWT를 통과한 로그인 유저 ID는 신뢰하고, 불필요한 SELECT를 줄이기 위해 프록시로 받는다.
        // fromMember = 로그인 유저(팔로우를 거는 사람)
        Member loginMember = memberService.getReferenceById(loginMemberId);
        // toMember = 팔로우 대상 유저(팔로우를 받는 사람)
        Member targetMember = memberService.findById(targetMemberId);

        // 자기 자신을 팔로우하는 것은 비즈니스 규칙상 허용하지 않는다.
        if (loginMember.getId().equals(targetMember.getId())) {
            throw new FollowException(FollowErrorCode.CANNOT_FOLLOW_SELF);
        }

        // 같은 (fromMember, toMember) 관계가 이미 있으면 중복 팔로우이므로 예외 처리한다.
        if (followRepository.existsByFromMemberAndToMember(loginMember, targetMember)) {
            throw new FollowException(FollowErrorCode.ALREADY_FOLLOWING);
        }

        // 셀프 조인 관계를 엔티티 한 건으로 표현한다.
        Follow follow = Follow.create(loginMember, targetMember);
        followRepository.save(follow);

        // 저장 직후 대상 유저의 팔로워 수를 다시 계산해 프론트가 화면 숫자를 즉시 갱신할 수 있게 한다.
        long followerCount = followRepository.countByToMember(targetMember);
        return FollowStatusResponse.of(targetMember.getId(), true, followerCount);
    }

    /**
     * 언팔로우 API.
     *
     * 흐름:
     * 1) 로그인 유저와 대상 유저를 조회한다.
     * 2) 실제로 팔로우 관계가 존재하는지 확인한다.
     * 3) 있으면 삭제한다.
     * 4) 삭제 후 대상 유저의 최신 팔로워 수를 다시 조회해 응답에 담는다.
     *
     * 언팔로우도 같은 전략을 따른다.
     * - 로그인 유저는 JWT 기준 프록시 사용
     * - 대상 유저는 실제 존재 검증을 위해 findById() 사용
     */
    @Transactional
    public FollowStatusResponse unfollow(Long loginMemberId, Long targetMemberId) {
        Member loginMember = memberService.getReferenceById(loginMemberId);
        Member targetMember = memberService.findById(targetMemberId);

        // 존재하지 않는 관계를 지우려 하면 "삭제할 대상이 없다"는 의미의 예외를 던진다.
        if (!followRepository.existsByFromMemberAndToMember(loginMember, targetMember)) {
            throw new FollowException(FollowErrorCode.FOLLOW_NOT_FOUND);
        }

        followRepository.deleteByFromMemberAndToMember(loginMember, targetMember);

        // 언팔로우 후 줄어든 팔로워 수를 프론트가 바로 반영할 수 있게 함께 내려준다.
        long followerCount = followRepository.countByToMember(targetMember);
        return FollowStatusResponse.of(targetMember.getId(), false, followerCount);
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

    /**
     * 로그인 유저가 특정 대상 유저를 팔로우 중인지 여부를 조회한다.
     */
    public boolean isFollowing(Member loginMember, Member targetMember) {
        if (loginMember.getId().equals(targetMember.getId())) {
            return false;
        }
        return followRepository.existsByFromMemberAndToMember(loginMember, targetMember);
    }
}
