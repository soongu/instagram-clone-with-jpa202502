package com.example.instagramclone.core.util;

import com.example.instagramclone.domain.follow.domain.Follow;
import com.example.instagramclone.domain.follow.domain.FollowRepository;
import com.example.instagramclone.domain.member.domain.Member;
import com.example.instagramclone.domain.member.domain.MemberRepository;
import com.example.instagramclone.domain.post.domain.Post;
import com.example.instagramclone.domain.post.domain.PostImage;
import com.example.instagramclone.domain.post.domain.PostImageRepository;
import com.example.instagramclone.domain.post.domain.PostLike;
import com.example.instagramclone.domain.post.domain.PostLikeRepository;
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

    private static final List<MemberSeed> MEMBER_SEEDS = List.of(
            new MemberSeed("kuromi", "쿠로미",
                    "https://slbs.shop/web/product/big/202403/ac79148d83434f2d513be1318fe6a8c0.jpg"),
            new MemberSeed("mamel", "마이멜로디",
                    "https://thumbnail8.coupangcdn.com/thumbnails/remote/492x492ex/image/retail/images/7129265199492958-abf55714-20d0-4e3c-89cd-218a7c7b177d.jpg"),
            new MemberSeed("pikachu", "피카츄",
                    "https://mblogthumb-phinf.pstatic.net/20160817_259/retspe_14714118890125sC2j_PNG/%C7%C7%C4%AB%C3%F2_%281%29.png?type=w800"),
            new MemberSeed("kitty", "키티",
                    "https://img.khan.co.kr/news/2014/11/03/l_2014110401000298000026001.jpg"),
            new MemberSeed("heartping", "하츄핑",
                    "https://i.ytimg.com/vi/NLfP4ewUf1c/oardefault.jpg")
    );

    private final MemberRepository memberRepository;
    private final FollowRepository followRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final PostLikeRepository postLikeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) throws Exception {
        if (memberRepository.count() > 0) return;

        String password = passwordEncoder.encode("abc1234!");
        List<Member> savedMembers = new ArrayList<>();
        List<List<Post>> postsByMember = new ArrayList<>();

        for (int i = 0; i < MEMBER_SEEDS.size(); i++) {
            MemberSeed seed = MEMBER_SEEDS.get(i);

            Member member = Member.builder()
                    .username(seed.username())
                    .password(password)
                    .name(seed.name())
                    .email(seed.username() + "@test.com")
                    .profileImageUrl(seed.profileImageUrl())
                    .build();

            Member savedMember = memberRepository.save(member);
            savedMembers.add(savedMember);
            List<Post> savedPostsOfMember = new ArrayList<>();

            for (int p = 1; p <= 5; p++) {
                Post post = Post.builder()
                        .content(savedMember.getName() + "의 " + p + "번째 일상 피드입니다~! #테스트")
                        .writer(savedMember)
                        .build();
                Post savedPost = postRepository.save(post);
                savedPostsOfMember.add(savedPost);

                for (int img = 1; img <= 2; img++) {
                    PostImage postImage = PostImage.builder()
                            .post(savedPost)
                            .imageUrl("https://picsum.photos/600/600?random=" + (i * 10 + p * 2 + img))
                            .imgOrder(img)
                            .build();
                    postImageRepository.save(postImage);
                }
            }

            postsByMember.add(savedPostsOfMember);
        }

        // 팔로우 테스트용 관계망 구성
        // kuromi -> mamel, pikachu, heartping
        // mamel -> kuromi, kitty
        // pikachu -> kuromi, mamel, heartping
        // kitty -> kuromi, mamel, heartping
        // heartping -> kitty
        seedFollowRelations(savedMembers);

        // 좋아요 테스트용 관계망 구성
        // 각 회원의 대표 게시글 몇 개에 서로 다른 회원들이 좋아요를 누른 상황을 만든다.
        seedPostLikeRelations(savedMembers, postsByMember);

        System.out.println("테스트용 계정 5개, 팔로우 관계, 좋아요 관계, 피드 세팅 완료! (게시물 총 25개)");
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

    private void seedPostLikeRelations(List<Member> members, List<List<Post>> postsByMember) {
        Member kuromi = members.get(0);
        Member mamel = members.get(1);
        Member pikachu = members.get(2);
        Member kitty = members.get(3);
        Member heartping = members.get(4);

        // 각 회원의 게시글에 좋아요가 고르게 퍼져 보이도록
        // "인기글", "중간 반응 글", "조용한 글"이 섞이게 구성한다.

        // kuromi 게시글들
        addLike(mamel, postsByMember.get(0).get(0));
        addLike(pikachu, postsByMember.get(0).get(0));
        addLike(kitty, postsByMember.get(0).get(0));
        addLike(heartping, postsByMember.get(0).get(1));
        addLike(mamel, postsByMember.get(0).get(2));
        addLike(kitty, postsByMember.get(0).get(3));

        // mamel 게시글들
        addLike(kuromi, postsByMember.get(1).get(0));
        addLike(pikachu, postsByMember.get(1).get(0));
        addLike(kitty, postsByMember.get(1).get(1));
        addLike(heartping, postsByMember.get(1).get(1));
        addLike(kuromi, postsByMember.get(1).get(4));

        // pikachu 게시글들
        addLike(kuromi, postsByMember.get(2).get(0));
        addLike(mamel, postsByMember.get(2).get(0));
        addLike(heartping, postsByMember.get(2).get(0));
        addLike(kitty, postsByMember.get(2).get(2));
        addLike(kuromi, postsByMember.get(2).get(3));
        addLike(mamel, postsByMember.get(2).get(4));

        // kitty 게시글들
        addLike(kuromi, postsByMember.get(3).get(0));
        addLike(heartping, postsByMember.get(3).get(0));
        addLike(mamel, postsByMember.get(3).get(1));
        addLike(pikachu, postsByMember.get(3).get(1));
        addLike(kuromi, postsByMember.get(3).get(4));

        // heartping 게시글들
        addLike(kuromi, postsByMember.get(4).get(0));
        addLike(mamel, postsByMember.get(4).get(0));
        addLike(kitty, postsByMember.get(4).get(0));
        addLike(pikachu, postsByMember.get(4).get(2));
        addLike(mamel, postsByMember.get(4).get(3));
        addLike(kitty, postsByMember.get(4).get(4));
    }

    private void addLike(Member member, Post post) {
        postLikeRepository.save(PostLike.create(member, post));
        // post_like 레코드와 비정규화 likeCount를 함께 맞춰 둔다.
        post.changeLikeCountBy(1);
    }

    /**
     * 테스트 회원 1명의 고정 데이터를 묶어두는 record.
     *
     * 기존처럼 username / name / profileUrl 을 각각 따로 관리하면
     * 인덱스가 어긋났을 때 버그를 찾기 어렵다.
     * 따라서 한 회원의 데이터를 하나의 객체로 묶어두는 편이 안전하다.
     */
    private record MemberSeed(
            String username,
            String name,
            String profileImageUrl
    ) {
    }
}
