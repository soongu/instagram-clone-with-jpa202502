package com.example.instagramclone.domain.follow.entity;

import com.example.instagramclone.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "follows",
        indexes = {
                @Index(name = "idx_follows_follower", columnList = "follower_id"),
                @Index(name = "idx_follows_following", columnList = "following_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_follow",
                        columnNames = {"follower_id", "following_id"}
                )
        }
)
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"follower", "following"})
public class Follow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private Member follower; // 팔로우를 받는 사용자 (팔로워)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private Member following; // 팔로우를 하는 사용자 (팔로잉)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Follow(Member follower, Member following) {
        this.follower = follower;
        this.following = following;
    }

    // 정적 팩토리 메서드
    public static Follow of(Member follower, Member following) {
        return Follow.builder()
                .follower(follower)
                .following(following)
                .build();
    }
}