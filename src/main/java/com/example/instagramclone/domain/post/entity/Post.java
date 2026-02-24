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
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> images = new ArrayList<>();

    @Builder
    public Post(String content, Member writer) {
        this.content = content;
        this.writer = writer;
    }

    // TODO: [Day 7] 양방향 연관관계 편의 메서드(addImage)를 추가하세요.
    public void addImage(PostImage image) {
        this.images.add(image);
        // 편의상 양방향 관계 설정 (PostImage 측 세터가 없으므로 생략하거나, Builder로 주입된 것을 가정함)
    }
}
