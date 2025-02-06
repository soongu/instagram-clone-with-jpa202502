package com.example.instagramclone.repository.custom;

import com.example.instagramclone.domain.post.dto.response.ProfilePostResponse;
import com.example.instagramclone.domain.post.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {
    // 전체 피드 목록 조회 (이미지, 작성자 정보 포함)
    Slice<Post> findAllWithMemberAndImages(Pageable pageable);

    // 특정 사용자의 프로필 페이지 전용 피드 목록 조회
//    List<ProfilePostResponse> findProfilePosts(Long memberId);

    // 단일 피드 상세조회 - querydsl로 처리
    Optional<Post> findPostDetailById(Long postId);

    // 팔로잉 기반 피드 조회
    Slice<Post> findFeedPosts(
            Long memberId,
            Pageable pageable
    );

    // 팔로잉이 없는 경우를 위한 추천 피드 조회
    Slice<Post> findRecommendedPosts(Pageable pageable);

    // 특정 해시태그 페이지 전용 피드 목록 조회
    List<ProfilePostResponse> findPostsByHashtag(
            String tagName
            , int offset
            , int limit
    );

    // 특정 사용자의 프로필 페이지 전용 피드 목록 조회
    List<ProfilePostResponse> findProfilePosts(Long memberId);
}
