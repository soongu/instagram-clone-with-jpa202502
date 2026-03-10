package com.example.instagramclone.domain.member.infrastructure;

import com.example.instagramclone.domain.member.domain.Member;
import com.example.instagramclone.domain.member.domain.MemberRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * MemberRepositoryCustom의 QueryDSL 구현체입니다.
 *
 * [네이밍 컨벤션 필수 준수!]
 * 이 클래스 이름은 반드시 "{통합 레포지토리 인터페이스명}Impl" 이어야 합니다.
 *   → MemberRepository + Impl = "MemberRepositoryImpl"
 *
 * "MemberRepositoryCustomImpl" 처럼 Custom 이름으로 짓거나
 * 다른 이름으로 짓는 순간 스프링이 이 구현체를 찾지 못합니다!
 * (런타임에 UnsatisfiedDependencyException 발생)
 */
@Repository
@RequiredArgsConstructor
@SuppressWarnings("unused") // queryFactory는 TODO 구현 후 사용됩니다
public class MemberRepositoryImpl implements MemberRepositoryCustom {

//    private final JPAQueryFactory queryFactory;

    // TODO: 1. QMember 정적 임포트 및 searchByUsername 메서드를 구현하세요.
    //
    //         [QueryDSL 작성 순서]
    //         ① Q클래스 가져오기:  QMember member = QMember.member;
    //         ② 쿼리 작성:
    //            return queryFactory
    //                .selectFrom(member)
    //                .where(member.username.containsIgnoreCase(keyword))
    //                .fetch();
    //
    //         [JPQL과의 비교]
    //         JPQL: "SELECT m FROM Member m WHERE LOWER(m.username) LIKE LOWER(CONCAT('%',:keyword,'%'))"
    //         → 문자열이라 오타가 있어도 컴파일이 통과되고 런타임에 터집니다.
    //         QueryDSL: 자바 코드이므로 오타가 있으면 컴파일 에러로 즉시 탐지됩니다. ✅
    //
    //         [compileJava를 먼저 실행해야 QMember가 생성됩니다]
    //         터미널: ./gradlew compileJava
    //         IntelliJ: Build > Build Project (또는 망치 아이콘)
    @Override
    public List<Member> searchByUsername(String keyword) {
        // TODO: 위 힌트를 참고하여 구현하세요.
        return List.of();
    }
}
