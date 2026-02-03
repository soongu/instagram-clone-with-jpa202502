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
                @Index(name = "idx_follows_from_member", columnList = "from_member_id"),
                @Index(name = "idx_follows_to_member", columnList = "to_member_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_follow",
                        columnNames = {"from_member_id", "to_member_id"}
                )
        }
)
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"fromMember", "toMember"})
public class Follow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_member_id", nullable = false)
    private Member fromMember; // 팔로우를 하는 사용자 (Follower)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_member_id", nullable = false)
    private Member toMember; // 팔로우를 받는 사용자 (Following)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private Follow(Member fromMember, Member toMember) {
        this.fromMember = fromMember;
        this.toMember = toMember;
    }

    // 정적 팩토리 메서드
    public static Follow of(Member fromMember, Member toMember) {
        return Follow.builder()
                .fromMember(fromMember)
                .toMember(toMember)
                .build();
    }
}