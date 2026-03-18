package com.example.instagramclone.domain.follow.application;

import com.example.instagramclone.core.exception.FollowErrorCode;
import com.example.instagramclone.core.exception.FollowException;
import com.example.instagramclone.domain.follow.api.FollowStatusResponse;
import com.example.instagramclone.domain.follow.domain.Follow;
import com.example.instagramclone.domain.follow.domain.FollowRepository;
import com.example.instagramclone.domain.member.application.MemberService;
import com.example.instagramclone.domain.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/**
 * FollowService 단위 테스트.
 *
 * [테스트 범위]
 * - follow(): 자기 자신 팔로우 금지, 중복 팔로우 방지, 저장 및 followerCount 반환
 * - unfollow(): 관계 미존재 예외, 삭제 및 followerCount 반환
 */
@ExtendWith(MockitoExtension.class)
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private FollowService followService;

    private Member buildMockMember(Long id, String username) {
        Member member = Member.builder()
                .username(username)
                .password("encoded_pw")
                .email(username + "@test.com")
                .name("테스트 유저")
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    @Nested
    @DisplayName("follow()")
    class FollowUser {

        @Test
        @DisplayName("실패 - 자기 자신을 팔로우하려 하면 CANNOT_FOLLOW_SELF 예외")
        void fail_cannot_follow_self() {
            Long loginMemberId = 1L;
            Member sameMember = buildMockMember(1L, "me");

            given(memberService.getReferenceById(loginMemberId)).willReturn(sameMember);
            given(memberService.findById(1L)).willReturn(sameMember);

            assertThatThrownBy(() -> followService.follow(loginMemberId, 1L))
                    .isInstanceOf(FollowException.class)
                    .hasMessage(FollowErrorCode.CANNOT_FOLLOW_SELF.getMessage());

            then(followRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("실패 - 이미 팔로우 중이면 ALREADY_FOLLOWING 예외")
        void fail_already_following() {
            Long loginMemberId = 1L;
            Long targetMemberId = 2L;
            Member loginMember = buildMockMember(loginMemberId, "me");
            Member targetMember = buildMockMember(targetMemberId, "target");

            given(memberService.getReferenceById(loginMemberId)).willReturn(loginMember);
            given(memberService.findById(targetMemberId)).willReturn(targetMember);
            given(followRepository.existsByFromMemberAndToMember(loginMember, targetMember)).willReturn(true);

            assertThatThrownBy(() -> followService.follow(loginMemberId, targetMemberId))
                    .isInstanceOf(FollowException.class)
                    .hasMessage(FollowErrorCode.ALREADY_FOLLOWING.getMessage());

            then(followRepository).should(never()).save(any(Follow.class));
            then(followRepository).should(never()).countByToMember(any(Member.class));
        }

        @Test
        @DisplayName("성공 - 팔로우 저장 후 following=true 와 최신 followerCount 반환")
        void success_follow_returns_status_and_follower_count() {
            Long loginMemberId = 1L;
            Long targetMemberId = 2L;
            Member loginMember = buildMockMember(loginMemberId, "me");
            Member targetMember = buildMockMember(targetMemberId, "target");

            given(memberService.getReferenceById(loginMemberId)).willReturn(loginMember);
            given(memberService.findById(targetMemberId)).willReturn(targetMember);
            given(followRepository.existsByFromMemberAndToMember(loginMember, targetMember)).willReturn(false);
            given(followRepository.countByToMember(targetMember)).willReturn(11L);

            FollowStatusResponse response = followService.follow(loginMemberId, targetMemberId);

            assertThat(response.memberId()).isEqualTo(targetMemberId);
            assertThat(response.following()).isTrue();
            assertThat(response.followerCount()).isEqualTo(11L);

            ArgumentCaptor<Follow> captor = ArgumentCaptor.forClass(Follow.class);
            then(followRepository).should().save(captor.capture());
            assertThat(captor.getValue().getFromMember()).isSameAs(loginMember);
            assertThat(captor.getValue().getToMember()).isSameAs(targetMember);
            then(followRepository).should().countByToMember(targetMember);
        }
    }

    @Nested
    @DisplayName("unfollow()")
    class UnfollowUser {

        @Test
        @DisplayName("실패 - 팔로우 관계가 없으면 FOLLOW_NOT_FOUND 예외")
        void fail_follow_not_found() {
            Long loginMemberId = 1L;
            Long targetMemberId = 2L;
            Member loginMember = buildMockMember(loginMemberId, "me");
            Member targetMember = buildMockMember(targetMemberId, "target");

            given(memberService.getReferenceById(loginMemberId)).willReturn(loginMember);
            given(memberService.findById(targetMemberId)).willReturn(targetMember);
            given(followRepository.existsByFromMemberAndToMember(loginMember, targetMember)).willReturn(false);

            assertThatThrownBy(() -> followService.unfollow(loginMemberId, targetMemberId))
                    .isInstanceOf(FollowException.class)
                    .hasMessage(FollowErrorCode.FOLLOW_NOT_FOUND.getMessage());

            then(followRepository).should(never()).deleteByFromMemberAndToMember(any(Member.class), any(Member.class));
            then(followRepository).should(never()).countByToMember(any(Member.class));
        }

        @Test
        @DisplayName("성공 - 팔로우 관계 삭제 후 following=false 와 최신 followerCount 반환")
        void success_unfollow_returns_status_and_follower_count() {
            Long loginMemberId = 1L;
            Long targetMemberId = 2L;
            Member loginMember = buildMockMember(loginMemberId, "me");
            Member targetMember = buildMockMember(targetMemberId, "target");

            given(memberService.getReferenceById(loginMemberId)).willReturn(loginMember);
            given(memberService.findById(targetMemberId)).willReturn(targetMember);
            given(followRepository.existsByFromMemberAndToMember(loginMember, targetMember)).willReturn(true);
            given(followRepository.countByToMember(targetMember)).willReturn(10L);

            FollowStatusResponse response = followService.unfollow(loginMemberId, targetMemberId);

            assertThat(response.memberId()).isEqualTo(targetMemberId);
            assertThat(response.following()).isFalse();
            assertThat(response.followerCount()).isEqualTo(10L);

            then(followRepository).should().deleteByFromMemberAndToMember(loginMember, targetMember);
            then(followRepository).should().countByToMember(targetMember);
        }
    }
}
