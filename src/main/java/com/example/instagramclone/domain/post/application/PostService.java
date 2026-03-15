package com.example.instagramclone.domain.post.application;

import com.example.instagramclone.core.common.dto.FeedResponse;
import com.example.instagramclone.core.util.FileStore;
import com.example.instagramclone.domain.member.application.MemberService;
import com.example.instagramclone.domain.member.domain.Member;
import com.example.instagramclone.domain.post.api.PostCreateRequest;
import com.example.instagramclone.domain.post.api.PostResponse;
import com.example.instagramclone.domain.post.api.ProfilePostResponse;
import com.example.instagramclone.domain.post.domain.Post;
import com.example.instagramclone.domain.post.domain.PostImage;
import com.example.instagramclone.domain.post.domain.PostImageRepository;
import com.example.instagramclone.domain.post.domain.PostLike;
import com.example.instagramclone.domain.post.domain.PostLikeRepository;
import com.example.instagramclone.domain.post.domain.PostRepository;
import com.example.instagramclone.domain.post.infrastructure.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final MemberService memberService;
    private final FileStore fileStore;
    private final PostMapper postMapper;

    // ====================== 게시물 생성 ======================
    @Transactional
    public Long create(PostCreateRequest request, List<MultipartFile> images, Long loginMemberId) throws IOException {
        Member writer = memberService.getReferenceById(loginMemberId);

        Post post = Post.builder()
                .content(request.content())
                .writer(writer)
                .build();

        Post savedPost = postRepository.save(post);

        // 이미지 저장
        if (images != null && !images.isEmpty()) {
            List<PostImage> postImages = IntStream.range(0, images.size())
                    .mapToObj(i -> {
                        try {
                            String imageUrl = fileStore.storeFile(images.get(i));
                            return PostImage.builder()
                                    .post(savedPost)
                                    .imageUrl(imageUrl)
                                    .imgOrder(i + 1)
                                    .build();
                        } catch (IOException e) {
                            throw new RuntimeException("이미지 저장 중 오류 발생", e);
                        }
                    })
                    .toList();

            postImageRepository.saveAll(postImages);
        }

        return savedPost.getId();
    }

    // ====================== 메인 피드 (liked 포함) ======================
    /**
     * 메인 피드 조회 - 로그인한 사용자가 각 게시물에 좋아요를 눌렀는지(liked) 상태까지 함께 반환
     */
    public FeedResponse<PostResponse> getFeed(Pageable pageable, Long loginMemberId) {
        Slice<Post> postSlice = postRepository.findAllWithImages(pageable);
        return buildFeedResponseWithLiked(postSlice, loginMemberId);
    }

    // ====================== 회원별 게시물 조회 (프로필 피드용) ======================
    public FeedResponse<ProfilePostResponse> getMemberPosts(Long memberId, Pageable pageable) {
        Slice<Post> postSlice = postRepository.findAllByWriterId(memberId, pageable);
        return buildFeedResponse(postSlice, postMapper::toProfilePostResponse);
    }

    // ====================== 공통 빌더 메서드 ======================

    /**
     * liked 상태까지 포함한 FeedResponse 생성 (getFeed에서 사용)
     */
    private FeedResponse<PostResponse> buildFeedResponseWithLiked(Slice<Post> postSlice, Long loginMemberId) {
        List<Post> posts = postSlice.getContent();
        if (posts.isEmpty()) {
            return FeedResponse.of(postSlice.hasNext(), Collections.emptyList());
        }

        Member loginMember = memberService.getReferenceById(loginMemberId);
        Set<Long> likedPostIds = getLikedPostIds(loginMember, posts);

        Map<Post, List<PostImage>> imageMap = groupImagesByPost(posts);

        List<PostResponse> responses = posts.stream()
                .map(post -> postMapper.toResponse(
                        post,
                        getSortedImages(post, imageMap),
                        likedPostIds.contains(post.getId())))
                .toList();

        return FeedResponse.of(postSlice.hasNext(), responses);
    }

    /**
     * liked가 필요 없는 일반적인 FeedResponse 생성 (getMemberPosts 등에서 사용)
     */
    private <T> FeedResponse<T> buildFeedResponse(Slice<Post> postSlice, BiFunction<Post, List<PostImage>, T> toDto) {
        List<Post> posts = postSlice.getContent();
        if (posts.isEmpty()) {
            return FeedResponse.of(postSlice.hasNext(), Collections.emptyList());
        }

        Map<Post, List<PostImage>> imageMap = groupImagesByPost(posts);

        List<T> responses = posts.stream()
                .map(post -> toDto.apply(post, getSortedImages(post, imageMap)))
                .toList();

        return FeedResponse.of(postSlice.hasNext(), responses);
    }

    // ====================== 유틸 메서드 ======================

    private Set<Long> getLikedPostIds(Member loginMember, List<Post> posts) {
        return postLikeRepository.findByMemberAndPostIn(loginMember, posts).stream()
                .map(pl -> pl.getPost().getId())
                .collect(Collectors.toSet());
    }

    private Map<Post, List<PostImage>> groupImagesByPost(List<Post> posts) {
        return postImageRepository.findByPostIn(posts).stream()
                .collect(Collectors.groupingBy(PostImage::getPost));
    }

    private List<PostImage> getSortedImages(Post post, Map<Post, List<PostImage>> imageMap) {
        return imageMap.getOrDefault(post, Collections.emptyList()).stream()
                .sorted(Comparator.comparing(PostImage::getImgOrder))
                .toList();
    }
}