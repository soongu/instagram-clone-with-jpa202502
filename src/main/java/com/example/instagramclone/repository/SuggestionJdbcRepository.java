package com.example.instagramclone.repository;

import com.example.instagramclone.domain.member.dto.response.SuggestedMemberResponse;
import com.example.instagramclone.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SuggestionJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<SuggestedMemberResponse> findSuggestedMembers(Long currentMemberId, int limit) {
        String sql = """
            SELECT
                m.id,
                m.username,
                m.name,
                m.profile_image_url,
                (SELECT COUNT(*) FROM follows f WHERE f.to_member_id = m.id) AS follower_count,
                (SELECT COUNT(*) FROM posts p WHERE p.member_id = m.id) AS post_count,
                (
                    SELECT GROUP_CONCAT(common_m.username SEPARATOR ',')
                    FROM follows f_target
                    JOIN follows f_me ON f_target.from_member_id = f_me.to_member_id
                    JOIN users common_m ON f_target.from_member_id = common_m.id
                    WHERE f_target.to_member_id = m.id
                      AND f_me.from_member_id = ?
                ) AS common_followers
            FROM users m
            WHERE m.id != ?
              AND m.id NOT IN (
                  SELECT to_member_id FROM follows WHERE from_member_id = ?
              )
            ORDER BY m.created_at DESC, post_count DESC
            LIMIT ?
        """;

        return jdbcTemplate.query(sql, suggestionRowMapper(), currentMemberId, currentMemberId, currentMemberId, limit);
    }

    private RowMapper<SuggestedMemberResponse> suggestionRowMapper() {
        return (rs, rowNum) -> {
            // Member 엔티티 매핑
            Member member = Member.builder()
                    .username(rs.getString("username"))
                    .name(rs.getString("name"))
                    .profileImageUrl(rs.getString("profile_image_url"))
                    .build();

            long followerCount = rs.getLong("follower_count");
            long postCount = rs.getLong("post_count");
            String commonFollowersStr = rs.getString("common_followers");
            
            List<String> commonFollowers = commonFollowersStr != null
                    ? Arrays.asList(commonFollowersStr.split(","))
                    : Collections.emptyList();

            // 추천 이유 생성 로직 재사용 (Service 로직을 여기로 가져오거나, 메서드로 분리)
            String suggestionReason = createSuggestionReason(commonFollowers, followerCount, postCount);

            return SuggestedMemberResponse.of(member, suggestionReason);
        };
    }

    private String createSuggestionReason(List<String> commonFollowers, long followerCount, long feedCount) {
        if (!commonFollowers.isEmpty()) {
            if (commonFollowers.size() == 1) {
                return commonFollowers.get(0) + "님이 팔로우합니다";
            } else {
                return commonFollowers.get(0) + "님 외 " + (commonFollowers.size() - 1) + "명이 팔로우합니다";
            }
        } else if (followerCount > 2) {
            return "인기 있는 계정";
        } else if (feedCount > 2) {
            return "활발한 활동";
        } else {
            return "회원님을 위한 추천";
        }
    }
}
