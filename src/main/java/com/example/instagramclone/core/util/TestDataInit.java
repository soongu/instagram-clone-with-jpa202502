package com.example.instagramclone.core.util;

import com.example.instagramclone.domain.follow.domain.Follow;
import com.example.instagramclone.domain.follow.domain.FollowRepository;
import com.example.instagramclone.domain.member.domain.Member;
import com.example.instagramclone.domain.member.domain.MemberRepository;
import com.example.instagramclone.domain.post.domain.Post;
import com.example.instagramclone.domain.post.domain.PostImage;
import com.example.instagramclone.domain.post.domain.PostImageRepository;
import com.example.instagramclone.domain.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TestDataInit implements ApplicationRunner {

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
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
        List<Member> savedMembers = new ArrayList<>();

        for (int i = 0; i < usernames.size(); i++) {
            Member member = Member.builder()
                    .username(usernames.get(i))
                    .password(password)
                    .name(names.get(i))
                    .email(usernames.get(i) + "@test.com")
                    .build();

            Member savedMember = memberRepository.save(member);
            savedMembers.add(savedMember);

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

        // 팔로우 테스트용 관계망 구성
        // kuromi -> mamel, pikachu, heartping
        // mamel -> kuromi, kitty
        // pikachu -> kuromi, mamel, heartping
        // kitty -> kuromi, mamel, heartping
        // heartping -> kitty
        seedFollowRelations(savedMembers);

        System.out.println("테스트용 계정 5개, 팔로우 관계, 피드 세팅 완료! (게시물 총 25개)");
    }

    private void seedFollowRelations(List<Member> members) {
        Member kuromi = members.get(0);
        Member mamel = members.get(1);
        Member pikachu = members.get(2);
        Member kitty = members.get(3);
        Member heartping = members.get(4);

        followRepository.save(Follow.create(kuromi, mamel));
        followRepository.save(Follow.create(kuromi, pikachu));
        followRepository.save(Follow.create(kuromi, heartping));

        followRepository.save(Follow.create(mamel, kuromi));
        followRepository.save(Follow.create(mamel, kitty));

        followRepository.save(Follow.create(pikachu, kuromi));
        followRepository.save(Follow.create(pikachu, mamel));
        followRepository.save(Follow.create(pikachu, heartping));

        followRepository.save(Follow.create(kitty, kuromi));
        followRepository.save(Follow.create(kitty, mamel));
        followRepository.save(Follow.create(kitty, heartping));

        followRepository.save(Follow.create(heartping, kitty));
    }
}
