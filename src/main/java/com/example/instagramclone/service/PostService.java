package com.example.instagramclone.service;

import com.example.instagramclone.domain.post.dto.request.PostCreateRequest;
import com.example.instagramclone.domain.post.entity.Post;
import com.example.instagramclone.domain.post.entity.PostImage;
import com.example.instagramclone.repository.PostImageRepository;
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
    private final PostImageRepository postImageRepository;
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
                
        // 3. Post 저장: postRepository.save(post).
        Post savedPost = postRepository.save(post);
        
        // [기존 방식 - 강사 참고용 주석 처리]
        /*
        List<PostImage> postImages = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (int i = 0; i < images.size(); i++) {
                MultipartFile file = images.get(i);
                String imageUrl = fileStore.storeFile(file);
                
                PostImage postImage = PostImage.builder()
                        .post(savedPost)
                        .imageUrl(imageUrl)
                        .imgOrder(i + 1)
                        .build();
                        
                postImages.add(postImage);
            }
            postImageRepository.saveAll(postImages);
        }
        */

        // 4. 업로드된 이미지 파일들을 FileStore를 통해 저장하고 PostImage 엔티티 생성 (Stream & Lambda 적용)
        if (images != null && !images.isEmpty()) {
            List<PostImage> postImages = IntStream.range(0, images.size())
                    .mapToObj(i -> {
                        try {
                            // 5. FileStore를 이용해 실제 디스크에 저장 후 URL 반환
                            String imageUrl = fileStore.storeFile(images.get(i));
                            
                            // 6. PostImage 엔티티 생성 및 Post와 연관관계 설정
                            return PostImage.builder()
                                    .post(savedPost)
                                    .imageUrl(imageUrl)
                                    .imgOrder(i + 1)
                                    .build();
                        } catch (IOException e) {
                            // 람다 내부에서는 Checked Exception을 바로 던질 수 없으므로 RuntimeException으로 감싸서 던짐
                            throw new RuntimeException("피드 이미지 저장 중 오류가 발생했습니다.", e);
                        }
                    })
                    .toList(); // Java 16+ 에서는 .toList()로 불변 리스트 반환 가능
            
            // 7. 명시적으로 PostImage 엔티티들을 저장 (postImageRepository.saveAll) - Cascade 부재 체험!
            postImageRepository.saveAll(postImages);
        }
    }

    public List<Post> getFeed() {
        // TODO: 6. 데이터베이스에서 게시물을 모두 조회하여 반환
        // Hint: 이 메서드를 호출할 때 N+1 문제가 발생하는지 쿼리 로그를 주의 깊게 살펴봅니다.
        return postRepository.findAll();
    }
}
