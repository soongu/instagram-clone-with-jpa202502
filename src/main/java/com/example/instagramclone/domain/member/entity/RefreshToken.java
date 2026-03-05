package com.example.instagramclone.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TODO: [실습 5-1] memberId 와 token 필드를 선언하고, 생성자(@Builder)를 만들어주세요.

    public void updateToken(String newToken) {
        // TODO: [실습 6] 기존 토큰 값을 새로운 토큰 값으로 교체하는 메서드를 완성하세요.
    }
}
