package com.example.instagramclone.repository;

import com.example.instagramclone.domain.post.dto.response.ProfilePostResponse;
import com.example.instagramclone.domain.post.entity.Post;
import com.example.instagramclone.repository.custom.PostRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

//@Mapper
public interface PostRepository
    extends JpaRepository<Post, Long>, PostRepositoryCustom {

    // 피드 게시물 저장 - save기능으로 대체
//    void saveFeed(Post post);

    // 피드 이미지 저장 - PostImageRepository를 만들어 save기능 사용
//    void saveFeedImage(PostImage postImage);

    // 특정 피드에 첨부된 이미지 목록 조회 - 사실상 join으로 가져올거라 필요없음
//    List<PostImage> findImagesByPostId(Long postId);

    // 전체 피드 게시물 목록 조회 - queryDSL 처리
//    List<Post> findAll(
//            @Param("offset") int offset
//            , @Param("limit") int limit);

    // 특정 사용자의 피드 개수를 조회
    long countByMemberId(Long memberId);

    // 특정 사용자의 프로필 페이지 전용 피드 목록 조회
//    @Query(value = """
//            SELECT
//                p.id,
//                pi.image_url as mainThumbnail,
//                NVL(l.likeCount, 0) AS likeCount,
//                NVL(c.commentCount, 0) AS commentCount
//            FROM posts p
//            INNER JOIN (
//                SELECT post_id, image_url
//                FROM post_images
//                WHERE image_order = 1
//            ) pi ON p.id = pi.post_id
//            LEFT JOIN (
//                SELECT post_id, COUNT(*) AS likeCount
//                FROM post_likes
//                GROUP BY post_id
//            ) l ON p.id = l.post_id
//            LEFT JOIN (
//                SELECT post_id, COUNT(*) AS commentCount
//                FROM comments
//                GROUP BY post_id
//            ) c ON p.id = c.post_id
//            WHERE p.member_id = ?1
//            ORDER BY p.created_at DESC
//           """, nativeQuery = true)
//    List<ProfilePostResponse> findProfilePosts(Long memberId);

    // 특정 해시태그 페이지 전용 피드 목록 조회
//    @Query(value = """
//            SELECT
//                p.id,
//                pi.image_url           AS mainThumbnail,
//                NVL(l.likeCount, 0)    AS likeCount,
//                NVL(c.commentCount, 0) AS commentCount
//            FROM posts p
//            INNER JOIN post_hashtags ph ON p.id = ph.post_id
//            INNER JOIN (
//                    SELECT id, name
//                    FROM hashtags
//                    WHERE name = ?1
//            ) h ON ph.hashtag_id = h.id
//            LEFT OUTER JOIN
//                (SELECT post_id, image_url
//                FROM post_images
//                WHERE image_order = 1) pi ON p.id = pi.post_id
//            LEFT OUTER JOIN
//                (SELECT post_id, COUNT(*) AS likeCount
//                FROM post_likes
//                GROUP BY post_id) l ON p.id = l.post_id
//            LEFT OUTER JOIN
//                (SELECT post_id, COUNT(*) AS commentCount
//                FROM comments
//                GROUP BY post_id) c ON p.id = c.post_id
//            ORDER BY p.created_at DESC
//            LIMIT ?3 OFFSET ?2
//        """, nativeQuery = true)
//    List<ProfilePostResponse> findPostsByHashtag(
//            String tagName
//            , int offset
//            , int limit
//    );

    // 단일 피드 상세조회 - querydsl로 처리
//    Optional<Post> findPostDetailById(Long postId);


    // 팔로잉 기반 피드 조회 - querydsl로 처리
//    List<Post> findFeedPosts(
//            @Param("memberId") Long memberId,
//            @Param("offset") int offset,
//            @Param("limit") int limit
//    );

    // 팔로잉이 없는 경우를 위한 추천 피드 조회
//    List<Post> findRecommendedPosts(
//            @Param("offset") int offset,
//            @Param("limit") int limit
//    );

}
