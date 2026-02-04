package com.example.instagramclone.service;

import com.example.instagramclone.domain.comment.dto.response.CommentResponse;
import com.example.instagramclone.domain.hashtag.entity.Hashtag;
import com.example.instagramclone.domain.hashtag.entity.PostHashtag;
import com.example.instagramclone.domain.like.dto.response.LikeStatusResponse;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.domain.post.dto.request.PostCreate;
import com.example.instagramclone.domain.post.dto.response.FeedResponse;
import com.example.instagramclone.domain.post.dto.response.PostDetailResponse;
import com.example.instagramclone.domain.post.dto.response.PostResponse;
import com.example.instagramclone.domain.post.entity.Post;
import com.example.instagramclone.domain.post.entity.PostImage;
import com.example.instagramclone.exception.ErrorCode;
import com.example.instagramclone.exception.MemberException;
import com.example.instagramclone.exception.PostException;
import com.example.instagramclone.repository.*;
import com.example.instagramclone.util.HashtagUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository; // db에 피드내용 저장, 이미지저장
    private final HashtagRepository hashtagRepository; // 해시태그 db에 저장
    private final MemberRepository memberRepository; // 사용자 정보 가져오기
    private final PostLikeRepository postLikeRepository; // 좋아요 정보 가져오기
    private final CommentRepository commentRepository; // 댓글 정보 가져오기
    private final FollowRepository followRepository; // 팔로우 정보
    private final PostHashtagRepository postHashtagRepository;
    private final PostImageRepository postImageRepository;

    private final HashtagUtil hashtagUtil; // 해시태그 추출기

    // 피드 목록조회 중간처리
    public FeedResponse<PostResponse> findAllFeeds(String username, int size, int page) {

        Member foundMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));


        // 이 유저가 팔로잉 수가 하나라도 있는지 체크
        boolean hasFollowing
                = followRepository.countFollowByType(foundMember.getId(), "following") > 0;

        Pageable pageable = PageRequest.of(page - 1, size);

        Slice<Post> posts = hasFollowing
                ? postRepository.findFeedPosts(foundMember.getId(), pageable)
                : postRepository.findRecommendedPosts(pageable);

        List<Post> postList = posts.getContent();
        List<Long> postIds = postList.stream().map(Post::getId).collect(Collectors.toList());

        // Batch 1: 좋아요 여부 일괄 조회
        Set<Long> likedPostIds = postLikeRepository.findByMemberIdAndPostIdIn(foundMember.getId(), postIds)
                .stream()
                .map(pl -> pl.getPost().getId())
                .collect(Collectors.toSet());

        // Batch 2: 좋아요 수 일괄 조회 - Post.likeCount 필드로 대체되어 삭제됨
        // Map<Long, Long> likeCounts = ...

        // Batch 3: 댓글 수 일괄 조회
        Map<Long, Long> commentCounts = commentRepository.countCommentsByPostIdIn(postIds).stream()
                .collect(Collectors.toMap(
                        obj -> (Long) obj[0],
                        obj -> (Long) obj[1]
                ));

        // 전체 피드 조회 - 사이즈를 하나 더 크게 조회하여 다음 데이터가 있는지 체크
        List<PostResponse> feedList = postList.stream()
                .map(feed -> {
                    long postId = feed.getId();
                    boolean isLiked = likedPostIds.contains(postId);
                    long likeCount = feed.getLikeCount(); // Denormalization 최적화
                    long commentCount = commentCounts.getOrDefault(postId, 0L);

                    LikeStatusResponse likeStatus = LikeStatusResponse.of(isLiked, likeCount);
                    return PostResponse.of(feed, likeStatus, commentCount);
                })
                .collect(Collectors.toList());


        return FeedResponse.of(feedList, posts.hasNext());

    }


    // 피드 생성 DB에 가기 전 후 중간처리
    @Transactional
    public Long createFeed(PostCreate postCreate, List<String> imageUrls, String username) {

        // 유저의 이름을 통해 해당 유저의 ID를 구함
        Member foundMember = memberRepository.findByUsername(username)
                .orElseThrow(
                        () -> new MemberException(ErrorCode.MEMBER_NOT_FOUND)
                );

        // entity 변환 (Member 주입)
        Post post = postCreate.toEntity(foundMember);

        // 피드게시물을 posts테이블에 insert
        postRepository.save(post);

        // 이미지 관련 처리를 모두 수행
        Long postId = post.getId();

        processImages(imageUrls, post);

        // 해시태그 관련 처리를 수행
        processHashtags(post);

        // 컨트롤러에게 결과 반환
        return postId;
    }

    private void processHashtags(Post post) {
        // 1. 피드 내용에서 해시태그들을 모두 추출 (중복없이)
        Set<String> hashtagNames = hashtagUtil.extractHashtags(post.getContent());

        if (hashtagNames.isEmpty()) {
            return;
        }

        // 2. 기존 해시태그 조회 (Batch Select)
        List<Hashtag> existingHashtags = hashtagRepository.findByNameIn(hashtagNames);
        
        // 3. 새로운 해시태그 필터링 및 저장 (Batch Insert)
        Set<String> existingNames = existingHashtags.stream()
                .map(Hashtag::getName)
                .collect(Collectors.toSet());

        List<Hashtag> newHashtags = hashtagNames.stream()
                .filter(name -> !existingNames.contains(name))
                .map(name -> Hashtag.builder().name(name).build())
                .collect(Collectors.toList());

        if (!newHashtags.isEmpty()) {
            hashtagRepository.saveAll(newHashtags);
            existingHashtags.addAll(newHashtags);
        }

        // 4. PostHashtag 연결 (Batch Insert)
        List<PostHashtag> postHashtags = existingHashtags.stream()
                .map(hashtag -> PostHashtag.builder()
                        .post(post)
                        .hashtag(hashtag)
                        .build())
                .collect(Collectors.toList());

        postHashtagRepository.saveAll(postHashtags);
        log.debug("saved {} post hashtags", postHashtags.size());
    }

    private void processImages(List<String> imageUrls, Post post) {

        log.debug("start process Image!!");
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        // 이미지 파일 저장 및 PostImage 리스트 생성
        List<PostImage> postImages = IntStream.range(0, imageUrls.size())
                .mapToObj(i -> PostImage.builder()
                        .post(post)
                        .imageUrl(imageUrls.get(i))
                        .imageOrder(i + 1) // 0부터 시작하니 1을 더해줌
                        .build())
                .collect(Collectors.toList());

        // Batch Insert
        postImageRepository.saveAll(postImages);
        log.debug("saved {} post images", postImages.size());
    }

    // 피드 단일 조회 처리
    public PostDetailResponse getPostDetails(Long postId, String username) {
        Post post = postRepository.findPostDetailById(postId)
                .orElseThrow(
                        () -> new PostException(ErrorCode.POST_NOT_FOUND)
                );

        Member foundMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 좋아요 상태
        LikeStatusResponse likeStatus = LikeStatusResponse.of(
                postLikeRepository.findByPostIdAndMemberId(postId, foundMember.getId()).isPresent()
                , post.getLikeCount() // Denormalization 최적화
        );

        // 댓글 목록
        List<CommentResponse> commentResponses = commentRepository.findByPostIdWithMember(postId)
                .stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());

        return PostDetailResponse.of(
                post,
                likeStatus,
                commentResponses
        );
    }


}
