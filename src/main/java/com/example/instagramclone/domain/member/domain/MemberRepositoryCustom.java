package com.example.instagramclone.domain.member.domain;

import java.util.List;

/**
 * MemberRepository에 QueryDSL 기반 커스텀 쿼리를 추가하기 위한 인터페이스입니다.
 *
 * [커스텀 리포지토리 패턴이 필요한 이유]
 * Spring Data JPA가 제공하는 메서드(save, findById 등)와
 * 우리가 직접 작성하는 QueryDSL 쿼리를 하나의 MemberRepository로 합치기 위해
 * 이 인터페이스를 중간에 끼워 넣습니다.
 *
 * [통합 방법 - MemberRepository.java에서 아래와 같이 수정하세요]
 * public interface MemberRepository
 *     extends JpaRepository<Member, Long>, MemberRepositoryCustom { ... }
 */
public interface MemberRepositoryCustom {

    // TODO: 1. username에 keyword가 포함된 회원을 검색하는 메서드를 선언하세요.
    //
    //         [QueryDSL로 구현할 쿼리 미리보기]
    //         SELECT * FROM users WHERE LOWER(username) LIKE LOWER('%keyword%')
    //
    //         힌트: List<Member> searchByUsername(String keyword);
    List<Member> searchByUsername(String keyword);
}
