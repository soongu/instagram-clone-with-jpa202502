package com.example.instagramclone.repository;

import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.domain.member.entity.MemberRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// TDD의 시작: "실패하는 테스트를 먼저 작성하고, 이를 통과시키는 코드를 짠다."
// 하지만 우리는 이미 Entity와 Repository를 만들었으므로, "검증" 단계로 테스트를 진행합니다.

@DataJpaTest // JPA 관련 설정만 로드하여 빠르고 격리된 테스트 환경 제공 (Transaction Rollback 포함)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 저장 성공 테스트 - happy path")
    void saveMemberSuccess() {
        // given (준비): 테스트를 위한 데이터 준비
        Member member = Member.builder()
                .username("hongtutor")
                .password("1234")
                .email("hong@test.com")
                .name("Hong Gill Dong")
                .build();

        // when (실행): 실제 테스트할 로직 실행
        Member savedMember = memberRepository.save(member);

        // then (검증): 결과가 기대한 값과 같은지 확인
        assertThat(savedMember.getId()).isNotNull(); // ID가 생성되었는지 확인
        assertThat(savedMember.getUsername()).isEqualTo("hongtutor");
        assertThat(savedMember.getRole()).isEqualTo(MemberRole.USER); // Default 값 검증
    }

    @Test
    @DisplayName("이메일로 회원 존재 여부 확인 - existsByEmail")
    void existsByEmailTest() {
        // given
        Member member = Member.builder()
                .username("testuser")
                .password("password")
                .email("exist@test.com") // 중복 체크할 이메일
                .name("tester")
                .build();
        memberRepository.save(member);

        // when
        boolean exists = memberRepository.existsByEmail("exist@test.com");
        boolean notExists = memberRepository.existsByEmail("none@test.com");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("중복된 이메일 저장 시 예외 발생 - TDD 검증")
    void saveDuplicateEmailThrowUserException() {
        // given
        Member member1 = Member.builder()
                .username("user1")
                .password("1234")
                .email("same@test.com")
                .name("user1")
                .build();

        Member member2 = Member.builder()
                .username("user2")
                .password("1234")
                .email("same@test.com") // member1과 동일한 이메일 -> Unique 제약조건 위반 예상
                .name("user2")
                .build();

        memberRepository.save(member1); // 첫 번째 저장은 성공

        // when & then
        // DataIntegrityViolationException: DB 제약조건 위반 시 발생하는 Spring 예외
        assertThatThrownBy(() -> memberRepository.save(member2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
