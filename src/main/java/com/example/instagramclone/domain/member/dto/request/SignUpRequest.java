package com.example.instagramclone.domain.member.dto.request;

import com.example.instagramclone.domain.member.entity.Member;
import lombok.*;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    // TODO: 1. 유효성 검증 어노테이션을 추가하세요 (@NotBlank, @Email, @Pattern)
    
    private String emailOrPhone;
    private String name;
    private String username;
    private String password;

    public Member toEntity() {
        // TODO: 2. Member 엔터티로 변환하는 로직을 작성하세요
        return null;
    }
}
