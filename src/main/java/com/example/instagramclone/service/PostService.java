package com.example.instagramclone.service;

import com.example.instagramclone.domain.post.dto.request.PostCreateRequest;
import com.example.instagramclone.domain.post.entity.Post;
import com.example.instagramclone.repository.PostImageRepository;
import com.example.instagramclone.repository.PostRepository;
import com.example.instagramclone.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final FileStore fileStore;

    @Transactional
    public void create(PostCreateRequest request) {
        // TODO: 1. 세션에서 현재 로그인한 사용자 정보 가져오기 (Controller에서 전달받기 등)
        
        // TODO: 2. 업로드된 이미지 파일들을 FileStore를 통해 저장하고 고유 파일명 리스트 확보
        
        // TODO: 3. Post 엔티티를 생성하고 저장 (postRepository.save)
        
        // TODO: 4. 고유 파일명들을 이용해 PostImage 엔티티들을 생성하고 Post와 연관관계 설정
        
        // TODO: 5. 명시적으로 PostImage 엔티티들을 저장 (postImageRepository.saveAll) - Cascade 부재 체험!
    }

    public List<Post> getFeed() {
        // TODO: 6. 데이터베이스에서 게시물을 모두 조회하여 반환
        // Hint: 이 메서드를 호출할 때 N+1 문제가 발생하는지 쿼리 로그를 주의 깊게 살펴봅니다.
        return postRepository.findAll();
    }
}
