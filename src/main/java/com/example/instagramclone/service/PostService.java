package com.example.instagramclone.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.example.instagramclone.domain.common.dto.FeedResponse;
import com.example.instagramclone.domain.post.dto.response.PostResponse;

import com.example.instagramclone.domain.post.dto.request.PostCreateRequest;
import com.example.instagramclone.domain.post.entity.Post;
import com.example.instagramclone.domain.post.entity.PostImage;
import com.example.instagramclone.repository.PostRepository;
import com.example.instagramclone.repository.PostImageRepository;
import com.example.instagramclone.util.FileStore;
import com.example.instagramclone.domain.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
        // 1. 단방향 조회 전략: 부모(Post)만 페이징 조회합니다. (안전한 페이징)
        Slice<Post> postSlice = postRepository.findAllWithImages(pageable);
        List<Post> posts = postSlice.getContent();
        
        // 엣지 케이스 방어: 조회된 게시물이 없으면 빈 리스트로 즉시 반환하여 불필요한 IN 쿼리 방지
        if (posts.isEmpty()) {
            return FeedResponse.of(postSlice.hasNext(), Collections.emptyList());
        }
        
        // 2. 조회된 부모들을 기반으로 자식 이미지들을 IN 쿼리 한 방으로 전부 가져옵니다.
        List<PostImage> allImages = postImageRepository.findByPostIn(posts);
        
        // 3. 자식 이미지들을 부모의 ID(또는 엔티티)를 기준으로 메모리에서 그룹핑합니다.
        Map<Post, List<PostImage>> imageMap = allImages.stream()
                .collect(Collectors.groupingBy(PostImage::getPost));
        
        // 4. 응답을 엔티티에서 DTO(PostResponse)로 변환 (그룹핑된 이미지를 수동으로 조립 및 정렬)
        List<PostResponse> feedList = posts.stream()
                .map(post -> {
                    List<PostImage> postImages = imageMap.getOrDefault(post, Collections.emptyList())
                            .stream()
                            .sorted(Comparator.comparing(PostImage::getImgOrder))
                            .toList();
                    return PostResponse.of(post, postImages);
                })
                .toList();
                
        // slice.hasNext()를 통해 다음 페이지 여부 전달
        return FeedResponse.of(postSlice.hasNext(), feedList);
    }
}
