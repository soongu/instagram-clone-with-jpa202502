package com.example.instagramclone.domain.post.application;

import com.example.instagramclone.core.exception.PostErrorCode;
import com.example.instagramclone.core.exception.PostException;
import com.example.instagramclone.domain.member.application.MemberService;
import com.example.instagramclone.domain.member.domain.Member;
import com.example.instagramclone.domain.post.api.LikeStatusResponse;
import com.example.instagramclone.domain.post.domain.Post;
import com.example.instagramclone.domain.post.domain.PostLike;
import com.example.instagramclone.domain.post.domain.PostLikeRepository;
import com.example.instagramclone.domain.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [Day 12 Step 2] 좋아요 토글 API 로직.
 *
 * 이 단계에서는 "있는지 확인 → 있으면 삭제, 없으면 추가"만 다룹니다.
 * likeCount는 DB에서 COUNT(*)로 조회해 응답에 넣습니다.
 *
 */
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;

    /**
     * [Step 2] 좋아요 토글: 이미 누른 상태면 취소, 아니면 추가.
     *
     * 1) 게시물 조회 (없으면 404)
     * 2) 로그인 회원 참조 (FK만 필요하므로 getReferenceById)
     * 3) 이미 좋아요 했는지 존재 여부 조회
     * 4) 했으면 삭제 후 liked=false, 안 했으면 저장 후 liked=true
     * 5) 이 시점 기준으로 해당 게시물의 좋아요 개수를 COUNT 쿼리로 조회해 응답에 담음
     */
    @Transactional
    public LikeStatusResponse toggleLike(Long loginMemberId, Long postId) {
        // 1) 게시물 조회. 없으면 404(POST_NOT_FOUND)로 바로 응답.
        //    ※ Post는 getReferenceById 쓰지 않음. 프록시는 DB 안 가서 "없는 postId"를 검증 못 함.
        //       나중에 save(PostLike) 시 FK 위반으로 터지면 404가 아니라 500에 가까운 예외가 남.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));

        // 2) 로그인한 회원은 FK만 필요하므로 getReferenceById(프록시). SELECT 한 번 생략.
        Member member = memberService.getReferenceById(loginMemberId);

        // 3) "이 회원이 이 게시물에 이미 좋아요를 눌렀는지" 한 번에 판단
        boolean alreadyLiked = postLikeRepository.existsByMemberAndPost(member, post);

        if (alreadyLiked) {
            // 4-a) 이미 눌렀으면 레코드 삭제 → 좋아요 취소
            postLikeRepository.deleteByMemberAndPost(member, post);
            long count = postLikeRepository.countByPost(post);
            return new LikeStatusResponse(false, count);
        } else {
            // 4-b) 안 눌렀으면 PostLike 레코드 한 건 저장 → 좋아요 추가
            PostLike postLike = PostLike.create(member, post);
            postLikeRepository.save(postLike);
            long count = postLikeRepository.countByPost(post);
            return new LikeStatusResponse(true, count);
        }
    }
}
