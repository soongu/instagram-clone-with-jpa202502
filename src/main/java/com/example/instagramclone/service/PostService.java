package com.example.instagramclone.service;

import com.example.instagramclone.domain.post.dto.request.PostCreateRequest;
import com.example.instagramclone.domain.post.entity.Post;
import com.example.instagramclone.repository.PostRepository;
import com.example.instagramclone.repository.MemberRepository;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.exception.MemberErrorCode;
import com.example.instagramclone.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void create(PostCreateRequest request, List<MultipartFile> images, Long loginMemberId) throws IOException { // FileStore.storeFile throws IOException
        // 1. 요청 인가(Authorization): 세션에서 추출한 loginMemberId가 없는 경우 예외 발생시켜 접근 제한.
        if (loginMemberId == null) {
            throw new MemberException(MemberErrorCode.UNAUTHORIZED_ACCESS);
        }
        
        // 2. 세션의 회원 ID로 Member 영속성 컨텍스트 조립 후 Post 엔터티 생성 로직에 writer로 주입.
        Member writer = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
        
        Post post = Post.builder()
                .content(request.content())
                .writer(writer)
                .build();
                
        // 3. Post 저장: postRepository.save(post).
        Post savedPost = postRepository.save(post);
        
        // TODO: [Step 5] 업로드된 이미지 파일들을 FileStore를 통해 저장하고 고유 파일명 리스트 확보
        // TODO: [Step 5] PostImage 엔티티 생성 및 Post와 연관관계 설정
        // TODO: [Step 5] 명시적으로 PostImage 엔티티들을 저장 (postImageRepository.saveAll) - Cascade 부재 체험!
    }

    public List<Post> getFeed() {
        // TODO: 6. 데이터베이스에서 게시물을 모두 조회하여 반환
        // Hint: 이 메서드를 호출할 때 N+1 문제가 발생하는지 쿼리 로그를 주의 깊게 살펴봅니다.
        return postRepository.findAll();
    }
}
