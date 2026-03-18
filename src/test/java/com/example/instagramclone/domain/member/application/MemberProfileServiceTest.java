package com.example.instagramclone.domain.member.application;

import com.example.instagramclone.domain.follow.application.FollowService;
import com.example.instagramclone.domain.member.api.MemberProfileResponse;
import com.example.instagramclone.domain.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class MemberProfileServiceTest {

    @Mock
    private MemberService memberService;

    @Mock
    private FollowService followService;

    @InjectMocks
    private MemberProfileService memberProfileService;

    private Member buildMockMember(Long id, String username) {
        Member member = Member.builder()
                .username(username)
                .password("encoded_pw")
                .email(username + "@test.com")
                .name("테스트 유저")
                .profileImageUrl("/profiles/" + username + ".jpg")
                .build();
        ReflectionTestUtils.setField(member, "id", id);
        return member;
    }

    @Nested
    @DisplayName("getProfile()")
    class GetProfile {

        @Test
        @DisplayName("성공 - 자기 자신의 프로필이면 isFollowing=false, isCurrentUser=true")
        void success_my_profile_returns_false_and_me_true() {
            Long loginMemberId = 1L;
            Member me = buildMockMember(1L, "me");

            given(memberService.findById(1L)).willReturn(me);
            given(followService.isFollowing(loginMemberId, me)).willReturn(false);

            MemberProfileResponse response = memberProfileService.getProfile(loginMemberId, 1L);

            assertThat(response.memberId()).isEqualTo(1L);
            assertThat(response.username()).isEqualTo("me");
            assertThat(response.isFollowing()).isFalse();
            assertThat(response.isCurrentUser()).isTrue();
        }

        @Test
        @DisplayName("성공 - 다른 유저 프로필이면 FollowService.isFollowing 결과를 응답에 반영")
        void success_other_profile_uses_follow_service() {
            Long loginMemberId = 1L;
            Long memberId = 2L;
            Member targetMember = buildMockMember(memberId, "target");

            given(memberService.findById(memberId)).willReturn(targetMember);
            given(followService.isFollowing(loginMemberId, targetMember)).willReturn(true);

            MemberProfileResponse response = memberProfileService.getProfile(loginMemberId, memberId);

            assertThat(response.memberId()).isEqualTo(memberId);
            assertThat(response.username()).isEqualTo("target");
            assertThat(response.profileImageUrl()).isEqualTo("/profiles/target.jpg");
            assertThat(response.isFollowing()).isTrue();
            assertThat(response.isCurrentUser()).isFalse();

            then(followService).should().isFollowing(loginMemberId, targetMember);
        }
    }
}
