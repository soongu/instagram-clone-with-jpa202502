package com.example.instagramclone.domain.hashtag.entity;

import com.example.instagramclone.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_hashtags",
        indexes = {
                @Index(name = "idx_post_hashtags_post_id", columnList = "post_id"),
                @Index(name = "idx_post_hashtags_hashtag_id", columnList = "hashtag_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_post_hashtag",
                        columnNames = {"post_id", "hashtag_id"}
                )
        }
)
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"post", "hashtag"})
public class PostHashtag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id", nullable = false)
    private Hashtag hashtag;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    private PostHashtag(Post post, Hashtag hashtag) {
        this.post = post;
        this.hashtag = hashtag;
    }


}
