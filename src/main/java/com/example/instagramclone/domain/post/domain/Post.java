package com.example.instagramclone.domain.post.entity;

import com.example.instagramclone.global.common.BaseEntity;
import com.example.instagramclone.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;


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

    // [과제 2 예시답안] 현업 트렌드(연관관계 다이어트)를 반영하여 양방향 매핑 제거 (단방향 매핑으로 변경)
    // - cascade, orphanRemoval 옵션과 필드를 과감히 지워 도메인 결합도를 낮춤.
    // @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<PostImage> images = new ArrayList<>();

    @Builder
    public Post(String content, Member writer) {
        this.content = content;
        this.writer = writer;
    }

    // [과제 2 예시답안] 양방향 연관관계 편의 메서드 제거됨.
    // public void addImage(PostImage image) { ... }
}
