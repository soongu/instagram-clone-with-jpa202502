package com.example.instagramclone.service;

import com.example.instagramclone.domain.common.dto.FeedResponse;
import com.example.instagramclone.domain.post.dto.response.PostResponse;

import com.example.instagramclone.domain.post.dto.request.PostCreateRequest;
import com.example.instagramclone.domain.post.entity.Post;
import com.example.instagramclone.domain.post.entity.PostImage;
import com.example.instagramclone.repository.PostRepository;
import com.example.instagramclone.service.MemberService;
import com.example.instagramclone.util.FileStore;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.exception.MemberErrorCode;
import com.example.instagramclone.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberService memberService;
    private final FileStore fileStore;

    @Transactional
    public void create(PostCreateRequest request, List<MultipartFile> images, Long loginMemberId) throws IOException { // FileStore.storeFile throws IOException
        // 1. 요청 인가(Authorization): 세션에서 추출한 loginMemberId가 없는 경우 예외 발생시켜 접근 제한.
        if (loginMemberId == null) {
            throw new MemberException(MemberErrorCode.UNAUTHORIZED_ACCESS);
        }
        
        // 2. 세션의 회원 ID로 MemberService를 통해 Member 엔티티 획득.
        // 타 도메인의 Repository를 직접 참조하는 대신, Service 계층을 거쳐 의존성을 낮춤 (MSA 철학)
        Member writer = memberService.getMemberById(loginMemberId);
        
        Post post = Post.builder()
                .content(request.content())
                .writer(writer)
                .build();
                
        // 4. 업로드된 이미지 파일들을 FileStore를 통해 저장하고 PostImage 엔티티 생성 (Stream & Lambda 적용)
        if (images != null && !images.isEmpty()) {
            IntStream.range(0, images.size())
                    .forEach(i -> {
                        try {
                            // 5. FileStore를 이용해 실제 디스크에 저장 후 URL 반환
                            String imageUrl = fileStore.storeFile(images.get(i));
                            
                            // 6. PostImage 엔티티 생성 및 Post와 연관관계 설정
                            PostImage postImage = PostImage.builder()
                                    .post(post)
                                    .imageUrl(imageUrl)
                                    .imgOrder(i + 1)
                                    .build();
                            
                            // 양방향 연관관계 설정
                            post.addImage(postImage);
                        } catch (IOException e) {
                            // 람다 내부에서는 Checked Exception을 바로 던질 수 없으므로 RuntimeException으로 감싸서 던짐
                            throw new RuntimeException("피드 이미지 저장 중 오류가 발생했습니다.", e);
                        }
                    });
        }
        
        // 3. & 7. Post 저장: CascadeType.ALL 덕분에 post만 저장하면 연관된 postImage들도 한 번에 INSERT 됨.
        postRepository.save(post);
    }

    // TODO: [Day 7] 반환 타입을 FeedResponse<PostResponse> 로 변경하고, 인스타그램식 무한 스크롤(Paging) 스펙에 맞추어 페이징 처리하세요.
    public FeedResponse<PostResponse> getFeed() {
        // 데이터베이스에서 게시물을 모두 조회
        List<Post> posts = postRepository.findAll();
        
        // 응답을 엔티티에서 DTO(PostResponse)로 변환
        List<PostResponse> feedList = posts.stream()
                .map(PostResponse::from)
                .toList();
                
        // 임시로 hasNext는 false 고정, 페이징은 다음 스텝에서 적용
        return FeedResponse.of(false, feedList);
    }
}
