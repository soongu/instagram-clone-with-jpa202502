package com.example.instagramclone.infrastructure.persistence;

// TODO: 1. 이 클래스가 스프링 설정 클래스임을 명시하는 애노테이션을 추가하세요.
//         힌트: @Configuration
public class QueryDslConfig {

    // TODO: 2. JPA의 핵심 인터페이스인 EntityManager를 주입받는 필드를 선언하세요.
    //         JPA 표준 방식: @PersistenceContext
    //         (스프링 의존이 싫다면 @Autowired도 동작합니다)

    // TODO: 3. JPAQueryFactory를 스프링 Bean으로 등록하는 메서드를 작성하세요.
    //
    //         [JPAQueryFactory가 필요한 이유]
    //         QueryDSL은 JPAQueryFactory를 통해 타입 세이프한 쿼리를 작성합니다.
    //         이 Bean을 등록해 두면 어디서든 @Autowired / 생성자 주입으로 가져다 쓸 수 있습니다.
    //
    //         힌트:
    //         @Bean
    //         public JPAQueryFactory jpaQueryFactory() {
    //             return new JPAQueryFactory(entityManager);
    //         }
}
