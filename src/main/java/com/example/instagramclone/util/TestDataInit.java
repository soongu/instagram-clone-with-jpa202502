package com.example.instagramclone.util;

import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.repository.MemberRepository;
import com.example.instagramclone.domain.post.entity.Post;
import com.example.instagramclone.domain.post.entity.PostImage;
import com.example.instagramclone.repository.PostRepository;
import com.example.instagramclone.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataInit implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (memberRepository.count() > 0) return;

        List<String> usernames = List.of("kuromi", "mamel", "pikachu", "kitty", "heartping");
        List<String> names = List.of("쿠로미", "마이멜로디", "피카츄", "키티", "하츄핑");
        String password = passwordEncoder.encode("abc1234!");

        for (int i = 0; i < usernames.size(); i++) {
            Member member = Member.builder()
                    .username(usernames.get(i))
                    .password(password)
                    .name(names.get(i))
                    .email(usernames.get(i) + "@test.com")
                    .build();

            Member savedMember = memberRepository.save(member);

            // TODO: [Day 7] N+1 조회 실습을 위해 계정당 5개의 피드와 각 2개의 사진을 무작위로 생성 (picsum 서비스 이용)
            for (int p = 1; p <= 5; p++) {
                Post post = Post.builder()
                        .content(savedMember.getName() + "의 " + p + "번째 일상 피드입니다~! #테스트")
                        .writer(savedMember)
                        .build();
                Post savedPost = postRepository.save(post);

                for (int img = 1; img <= 2; img++) {
                    PostImage postImage = PostImage.builder()
                            .post(savedPost)
                            .imageUrl("https://picsum.photos/600/600?random=" + (i * 10 + p * 2 + img))
                            .imgOrder(img)
                            .build();
                    postImageRepository.save(postImage);
                }
            }
        }
        System.out.println("테스트용 계정 5개 및 피드 세팅 완료! (게시물 총 25개)");
    }
}
