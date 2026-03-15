package com.example.instagramclone.domain.post.domain;

import com.example.instagramclone.core.common.BaseEntity;
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

    /**
     * [Day 12 Part 2] 비정규화: 조회 시 COUNT(*) 대신 이 필드 사용.
     * 좋아요 추가 시 +1, 취소 시 -1. 
     */
    @Column(nullable = false)
    private int likeCount = 0;


    @Builder
    public Post(String content, Member writer) {
        this.content = content;
        this.writer = writer;
    }

    /**
     * [Day 12 Part 2] 비정규화 likeCount 갱신. 좋아요 추가 시 +1, 취소 시 -1.
     */
    public void changeLikeCountBy(int delta) {
        this.likeCount += delta;
    }

}
