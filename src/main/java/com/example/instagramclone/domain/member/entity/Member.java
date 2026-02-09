package com.example.instagramclone.domain.member.entity;

import lombok.*;

// TODO: 1. JPA Entity 설정을 추가하세요 (@Entity, @Table)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    // TODO: 2. ID 필드와 PK 설정을 추가하세요 (@Id, @GeneratedValue)
    private Long id;

    // TODO: 3. 컬럼 매핑과 제약조건을 설정하세요 (@Column)
    private String username;
    private String password;
    private String email;
    private String phone;
    private String name;
    private String profileImageUrl;
    
    private String role;
    
    // TODO: 4. Builder 패턴을 적용할 생성자를 만드세요
}
