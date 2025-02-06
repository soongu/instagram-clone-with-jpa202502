package com.example.instagramclone.domain.hashtag.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(
        name = "hashtags",
        indexes = {
                @Index(name = "idx_hashtag_name", columnList = "name")
        }
)
@Getter @NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = "postHashtags")
public class Hashtag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "hashtag")
    private Set<PostHashtag> postHashtags = new LinkedHashSet<>();

    @Builder
    private Hashtag(String name) {
        this.name = name;
    }
}
