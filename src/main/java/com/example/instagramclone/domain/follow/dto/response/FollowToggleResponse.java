package com.example.instagramclone.domain.follow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FollowToggleResponse {
    private boolean following;
    private long followerCount;

    public static FollowToggleResponse of(boolean following, long followerCount) {
        return FollowToggleResponse.builder()
                .following(following)
                .followerCount(followerCount)
                .build();
    }
}
