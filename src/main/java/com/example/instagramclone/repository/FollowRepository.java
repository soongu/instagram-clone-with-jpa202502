package com.example.instagramclone.repository;

import com.example.instagramclone.domain.follow.entity.Follow;
import com.example.instagramclone.repository.custom.FollowRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;



import java.util.List;


//@Mapper
public interface FollowRepository extends JpaRepository<Follow, Long>, FollowRepositoryCustom {

    // 팔로우 처리
//    void insert(Follow follow);

    // 언팔로우 처리
//    void delete(
//            @Param("followerId") Long followerId
//            , @Param("followingId") Long followingId
//    );

    void deleteByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);
    
    // Batch 조회: 로그인 유저가 특정 리스트의 유저들을 팔로우하고 있는지 확인
    List<Follow> findByFromMemberIdAndToMemberIdIn(Long fromMemberId, List<Long> toMemberIds);

    // 단건 조회
    java.util.Optional<Follow> findByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);

    // 팔로우 여부 확인
//    boolean doesFollowExist(
//            @Param("followerId") Long followerId
//            , @Param("followingId") Long followingId
//    );

    boolean existsByFromMemberIdAndToMemberId(Long fromMemberId, Long toMemberId);

    /**
     * 특정 유저의 팔로워 수 / 팔로잉 수 조회
     * @param userId - 현재 팔로잉 / 팔로워 수를 구하려는 회원의 ID
     * @param type - 현재 구하려는 정보가 팔로잉 수인지 팔로워 수인지 구분
     * @return - 해당 타입의 숫자
     */
//    long countFollowByType(
//            @Param("userId") Long userId
//            , @Param("type") String type
//    );

    // 특정 유저의 팔로워/팔로잉 유저 목록 조회
//    List<Follow> findFollowList(
//            @Param("userId") Long userId
//            , @Param("type") String type
//    );

    // 특정 사용자를 팔로우하는 사람들 중
    // 현재 사용자가 팔로우하는 사람들 목록 조회
//    List<String> findCommonFollowingUsernames(
//            @Param("targetUserId") Long targetUserId,
//            @Param("currentUserId") Long currentUserId
//    );
}
