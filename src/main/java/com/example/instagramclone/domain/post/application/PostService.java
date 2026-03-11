package com.example.instagramclone.domain.post.application;

import com.example.instagramclone.core.common.dto.FeedResponse;
import com.example.instagramclone.core.util.FileStore;
import com.example.instagramclone.domain.member.domain.Member;
import com.example.instagramclone.domain.member.application.MemberService;
import com.example.instagramclone.domain.post.api.PostCreateRequest;
import com.example.instagramclone.domain.post.api.PostResponse;
import com.example.instagramclone.domain.post.api.ProfilePostResponse;
import com.example.instagramclone.domain.post.domain.Post;
import com.example.instagramclone.domain.post.domain.PostImage;
import com.example.instagramclone.domain.post.domain.PostImageRepository;
import com.example.instagramclone.domain.post.domain.PostRepository;
import com.example.instagramclone.domain.post.infrastructure.PostMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;


import lombok.RequiredArgsConstructor;
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

        // Step 5: [실무 핵심] "토큰 안에 다 있는데 굳이 DB 가야 해? (getReferenceById)"
        // Q. memberService.getMemberById(loginMemberId) 를 쓰면 안 되나요?
        // A. 안 될 건 없지만, Post(자식)를 저장할 때 FK인 member_id 번호만 있으면 되는데,
        //    굳이 DB에 진짜 SELECT 쿼리를 날려서 Member(부모) 전체를 다 캐올 필요가 전혀 없습니다! (낭비)
        //
        // 고수의 방법: Hibernate의 프록시(가짜 객체) 기술을 활용하는 getReferenceById()를 씁니다.
        // 이 녀석은 DB를 전혀 찌르지 않고, 속이 텅 빈 가짜 객체(Proxy)에 ID 값 껍데기만 씌워서 가져옵니다.
        // 덕분에 불필요한 SELECT 쿼리 1회를 우아하게 없앨 수 있는 실무 최적화 기법입니다.
        // 추가 포인트: 다른 도메인의 Repository를 직접 주입받는 것은 MSA 철학에 어긋나므로, memberService를 통해 프록시를 받아옵니다.
        Member writer = memberService.getReferenceById(loginMemberId);

        Post post = Post.builder()
                .content(request.content())
                .writer(writer)
                .build();

        // 단방향 연관관계이므로 필드를 통한 Cascade를 쓸 수 없습니다.
        // 먼저 부모 엔티티(Post)를 저장합니다.
        Post savedPost = postRepository.save(post);

        // 4. 업로드된 이미지 파일들을 FileStore를 통해 저장하고 PostImage 엔티티 생성
        if (images != null && !images.isEmpty()) {
            List<PostImage> postImages = IntStream.range(0, images.size())
                    .mapToObj(i -> {
                        try {
                            String imageUrl = fileStore.storeFile(images.get(i));
                            return PostImage.builder()
                                    .post(savedPost) // 단방향이므로 연관관계의 주인인 PostImage에서 직접 Post를 세팅
                                    .imageUrl(imageUrl)
                                    .imgOrder(i + 1)
                                    .build();
                        } catch (IOException e) {
                            throw new RuntimeException("피드 이미지 저장 중 오류가 발생했습니다.", e);
                        }
                    })
                    .toList();

            // 자식 엔티티들을 명시적으로 Batch Save 합니다.
            postImageRepository.saveAll(postImages);
        }

        return savedPost.getId();
    }

    public FeedResponse<PostResponse> getFeed(Pageable pageable) {
        Slice<Post> postSlice = postRepository.findAllWithImages(pageable);
        return buildFeedResponse(postSlice, postMapper::toResponse);
    }

    public FeedResponse<ProfilePostResponse> getMemberPosts(Long memberId, Pageable pageable) {
        Slice<Post> postSlice = postRepository.findAllByWriterId(memberId, pageable);
        return buildFeedResponse(postSlice, postMapper::toProfilePostResponse);
    }

    /**
     * Post Slice를 FeedResponse로 변환하는 공통 로직.
     *
     * 이미지 IN 쿼리 → 그룹핑 → imgOrder 정렬까지 항상 동일하고,
     * 마지막 DTO 변환 방식만 호출부마다 다르므로 BiFunction으로 주입받습니다.
     * 덕분에 피드 타입이 추가되어도 이 메서드 하나로 재사용할 수 있습니다.
     */
    private <T> FeedResponse<T> buildFeedResponse(Slice<Post> postSlice, BiFunction<Post, List<PostImage>, T> toDto) {
        List<Post> posts = postSlice.getContent();

        if (posts.isEmpty()) {
            return FeedResponse.of(postSlice.hasNext(), Collections.emptyList());
        }

        Map<Post, List<PostImage>> imageMap = groupImagesByPost(posts);

        List<T> list = posts.stream()
                .map(post -> toDto.apply(post, getSortedImages(post, imageMap)))
                .toList();

        return FeedResponse.of(postSlice.hasNext(), list);
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
