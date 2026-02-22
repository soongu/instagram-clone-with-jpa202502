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

// TODO: 1. 엔티티 매핑 애노테이션을 작성하세요 (@Entity, @Table 등)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// TODO: 2. 테이블 이름을 "posts"로 설정하세요
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 3. content 필드를 작성하세요 (타입: String, TEXT 컬럼)
    private String content;

    // TODO: 4. Member와의 다대일 연관관계를 설정하세요 (필수: FetchType.LAZY)
    private Member writer;

    // TODO: 5. PostImage와의 일대다 양방향 연관관계를 설정하세요 (mappedBy = "post")
    // 주의: 실습을 위해 CascadeType은 설정하지 마세요!
    private List<PostImage> images = new ArrayList<>();

    // TODO: 6. 생성자를 작성하세요 (@Builder 활용)

}
