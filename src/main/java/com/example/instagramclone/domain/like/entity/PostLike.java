package com.example.instagramclone.domain.like.entity;

import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_likes",
        indexes = {
                @Index(name = "idx_post_likes_post_id", columnList = "post_id"),
                @Index(name = "idx_post_likes_member_id", columnList = "member_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_post_member",
                        columnNames = {"post_id", "member_id"}
                )
        }
)
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"post", "member"})
public class PostLike {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private PostLike(Post post, Member member) {
        this.post = post;
        this.member = member;
    }


    // 정적 팩토리 메서드
    public static PostLike of(Post post, Member member) {
        return PostLike.builder()
                .post(post)
                .member(member)
                .build();
    }
}
