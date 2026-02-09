package com.example.instagramclone.domain.post.entity;

import com.example.instagramclone.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    private Long id;
    private String content;
    private Long viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // TODO: Member와의 연관관계 설정을 추가하세요 (@ManyToOne)
    private Member member;

    private List<String> postImages = new ArrayList<>();
}
