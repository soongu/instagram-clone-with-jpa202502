package com.example.instagramclone.domain.post.entity;

import com.example.instagramclone.domain.common.BaseEntity;
import com.example.instagramclone.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member writer;

    // TODO: [Day 7] Cascade 적용 및 고아 객체 제거 옵션을 추가하세요. (cascade = CascadeType.ALL, orphanRemoval = true)
    @OneToMany(mappedBy = "post")
    private List<PostImage> images = new ArrayList<>();

    @Builder
    public Post(String content, Member writer) {
        this.content = content;
        this.writer = writer;
    }

    // TODO: [Day 7] 양방향 연관관계 편의 메서드(addImage)를 추가하세요.
    // public void addImage(PostImage image) { ... }
}
