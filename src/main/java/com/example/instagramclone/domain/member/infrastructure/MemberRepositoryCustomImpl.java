package com.example.instagramclone.domain.member.infrastructure;

import com.example.instagramclone.domain.member.domain.Member;
import com.example.instagramclone.domain.member.domain.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Repository;

/**
 * MemberRepositoryCustom의 QueryDSL 구현체.
 *
 * [네이밍 컨벤션 필수]
 * 클래스명 = "{커스텀 인터페이스명}Impl" → MemberRepositoryCustomImpl
 * Spring Data JPA는 fragment 인터페이스명 + "Impl" 접미사로 구현체를 탐색합니다.
 * 이름이 일치하지 않으면 커스텀 구현체를 찾지 못하고,
 * searchByUsername 같은 메서드를 JPA 쿼리 파생 메서드로 해석해 버립니다.
 *
 * [JPQL vs QueryDSL 비교]
 * JPQL:    "SELECT m FROM Member m WHERE LOWER(m.username) LIKE LOWER(CONCAT('%',:kw,'%'))"
 *          → 문자열이라 오타가 컴파일 시점에 잡히지 않음
 * QueryDSL: member.username.containsIgnoreCase(keyword)
 *           → 자바 코드이므로 오타 즉시 컴파일 에러
 */
@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> searchByUsername(String keyword) {
        QMember member = QMember.member;

        return queryFactory
                .selectFrom(member)
                .where(member.username.containsIgnoreCase(keyword))
                .fetch();
    }
}
