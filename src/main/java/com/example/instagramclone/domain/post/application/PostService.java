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
import com.example.instagramclone.domain.post.domain.PostRepository;
import com.example.instagramclone.domain.post.infrastructure.PostFeedRow;
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
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final MemberService memberService;
    private final FileStore fileStore;
    private final PostMapper postMapper;

    @Transactional
    public Long create(PostCreateRequest request, List<MultipartFile> images, Long loginMemberId) throws IOException {
        Member writer = memberService.getReferenceById(loginMemberId);

        Post post = Post.builder()
                .content(request.content())
                .writer(writer)
                .build();

        Post savedPost = postRepository.save(post);

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

    /**
     * 메인 피드. QueryDSL 1쿼리: Post + writer fetchJoin + EXISTS(post_like) 로 liked (과제).
     * 이미지는 기존처럼 findByPostIn 1회.
     */
    public FeedResponse<PostResponse> getFeed(Pageable pageable, Long loginMemberId) {
        Slice<PostFeedRow> slice = postRepository.findFeedWithLiked(pageable, loginMemberId);
        List<PostFeedRow> rows = slice.getContent();
        if (rows.isEmpty()) {
            return FeedResponse.of(slice.hasNext(), Collections.emptyList());
        }

        List<Post> posts = rows.stream().map(PostFeedRow::post).toList();
        Map<Post, List<PostImage>> imageMap = groupImagesByPost(posts);

        List<PostResponse> responses = rows.stream()
                .map(row -> postMapper.toResponse(
                        row.post(),
                        getSortedImages(row.post(), imageMap),
                        row.liked()))
                .toList();

        return FeedResponse.of(slice.hasNext(), responses);
    }

    public FeedResponse<ProfilePostResponse> getMemberPosts(Long memberId, Pageable pageable) {
        Slice<Post> postSlice = postRepository.findAllByWriterId(memberId, pageable);
        return buildFeedResponse(postSlice, postMapper::toProfilePostResponse);
    }

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
