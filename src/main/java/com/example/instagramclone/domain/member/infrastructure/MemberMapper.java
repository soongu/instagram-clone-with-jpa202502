package com.example.instagramclone.domain.member.infrastructure;

import com.example.instagramclone.domain.auth.api.SignUpResponse;
import com.example.instagramclone.domain.member.domain.Member;

// TODO: 1. MapStruct가 이 인터페이스의 구현체를 자동 생성하도록 애노테이션을 추가하세요.
//         @Mapper(componentModel = "spring")
//
//         [과제 2 안내]
//         오늘 강의에서 PostMapper를 완성했다면,
//         이 MemberMapper를 직접 작성해보는 것이 과제입니다.
//         특히 비밀번호 필드 제외 방법을 반드시 익혀두세요!
public interface MemberMapper {

    // TODO: 2. Member → SignUpResponse 매핑 메서드를 선언하세요.
    //
    //         [보안 핵심: password 필드는 절대 DTO에 포함하면 안 됩니다!]
    //         MapStruct에서 특정 필드를 매핑에서 제외하는 방법:
    //         @Mapping(target = "password", ignore = true)
    //
    //         [SignUpResponse 확인하기]
    //         현재 SignUpResponse는 username과 message 두 필드만 갖는 record입니다.
    //         Member에서 username만 가져오고, message는 별도로 설정해야 합니다.
    //         → @Mapping(target = "message", constant = "회원가입이 완료되었습니다.")  또는
    //            default 메서드로 직접 구현하는 방법도 있습니다.
    //
    //         힌트: SignUpResponse toSignUpResponse(Member member);

    // TODO: 위 가이드를 참고하여 메서드를 선언하고 @Mapping 애노테이션을 추가하세요.
    SignUpResponse toSignUpResponse(Member member);
}
