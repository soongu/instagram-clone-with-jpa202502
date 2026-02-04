package com.example.instagramclone.repository;

import com.example.instagramclone.domain.member.dto.response.MemberSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class SearchJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<MemberSearchResponse> searchMembers(String keyword, Long currentMemberId, int limit) {
        String sql = """
            SELECT
                m.username,
                m.name,
                m.profile_image_url,
                (
                    SELECT GROUP_CONCAT(common_m.username SEPARATOR ',')
                    FROM follows f_target
                    JOIN follows f_me ON f_target.from_member_id = f_me.to_member_id
                    JOIN users common_m ON f_target.from_member_id = common_m.id
                    WHERE f_target.to_member_id = m.id
                      AND f_me.from_member_id = ?
                ) AS common_followers
            FROM users m
            WHERE m.username LIKE ?
            ORDER BY m.username ASC
            LIMIT ?
        """;

        String searchPattern = "%" + keyword + "%";

        return jdbcTemplate.query(
                sql,
                searchRowMapper(),
                currentMemberId,
                searchPattern,
                limit
        );
    }

    private RowMapper<MemberSearchResponse> searchRowMapper() {
        return (rs, rowNum) -> {
            String commonFollowersStr = rs.getString("common_followers");
            
            List<String> commonFollowers = commonFollowersStr != null
                    ? Arrays.asList(commonFollowersStr.split(","))
                    : Collections.emptyList();

            return MemberSearchResponse.builder()
                    .username(rs.getString("username"))
                    .name(rs.getString("name"))
                    .profileImageUrl(rs.getString("profile_image_url"))
                    .commonFollowers(commonFollowers)
                    .build();
        };
    }
}
