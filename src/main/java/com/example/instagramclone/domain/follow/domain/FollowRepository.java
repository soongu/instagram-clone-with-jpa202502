package com.example.instagramclone.domain.follow.domain;

import com.example.instagramclone.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Follow 엔티티 전용 Repository.
 *
 * Day 13에서는 "팔로우 관계를 저장/삭제하는 쿼리"와
 * "프로필/리스트 화면에 뿌릴 조회 쿼리"를 함께 다루게 된다.
 * Follow는 Member -> Member 셀프 조인이므로,
 * 메서드 이름만 보고도 from / to 방향이 바로 읽히도록 설계하는 것이 중요.
 */
public interface FollowRepository extends JpaRepository<Follow, Long> {

    /** 로그인 유저(fromMember)가 대상 유저(toMember)를 이미 팔로우 중인지 확인한다. */
    boolean existsByFromMemberAndToMember(Member fromMember, Member toMember);

    /** 언팔로우 시, 정확히 한 건의 팔로우 관계(from -> to)를 삭제한다. */
    void deleteByFromMemberAndToMember(Member fromMember, Member toMember);

    /** 특정 유저가 "몇 명을 팔로우하고 있는지" 세는 쿼리. 즉, 팔로잉 수. */
    long countByFromMember(Member fromMember);

    /** 특정 유저를 "몇 명이 팔로우하고 있는지" 세는 쿼리. 즉, 팔로워 수. */
    long countByToMember(Member toMember);

    /**
     * 특정 유저를 팔로우하는 사람들의 Follow 목록 조회.
     * toMember = 프로필 주인, fromMember = 팔로워들
     */
    List<Follow> findAllByToMember(Member member);

    /**
     * 특정 유저가 팔로우하고 있는 사람들의 Follow 목록 조회.
     * fromMember = 프로필 주인, toMember = 그 유저가 팔로우하는 대상들
     */
    List<Follow> findAllByFromMember(Member member);

    /**
     * 리스트 응답 최적화를 위한 배치 조회용 메서드.
     *
     * 예: "로그인 유저 A가, 현재 목록에 보이는 여러 유저들을 각각 팔로우 중인가?"
     * 를 N번 existsBy... 호출하지 않고 한 번에 확인하기 위해 사용한다.
     *
     * fromMember = 로그인 유저
     * targetMembers = 화면에 뿌릴 대상 유저 엔티티 목록
     */
    List<Follow> findAllByFromMemberAndToMemberIn(Member fromMember, List<Member> targetMembers);
}
