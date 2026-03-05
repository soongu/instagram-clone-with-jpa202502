package com.example.instagramclone.repository;

import com.example.instagramclone.domain.member.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // TODO: [실습 5-2] Token 문자열로 RefreshToken 엔티티를 찾는 메서드(findByToken)를 선언하세요.
    // Optional<RefreshToken> find...
    
}
