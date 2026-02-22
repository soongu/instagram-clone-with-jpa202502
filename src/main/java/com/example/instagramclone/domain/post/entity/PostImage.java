package com.example.instagramclone.domain.post.entity;

import com.example.instagramclone.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

// TODO: 1. 엔티티 매핑 애노테이션을 작성하세요 (@Entity, @Table 등)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// TODO: 2. 테이블 이름을 "post_images"로 설정하세요
public class PostImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: 3. imageUrl과 imgOrder 필드를 작성하세요
    private String imageUrl;
    private Integer imgOrder;

    // TODO: 4. Post와의 다대일 연관관계를 설정하세요 (필수: FetchType.LAZY)
    private Post post;

    // TODO: 5. 생성자를 작성하세요 (@Builder 활용)

}
