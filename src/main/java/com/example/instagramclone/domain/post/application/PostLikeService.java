package com.example.instagramclone.domain.post.application;

import com.example.instagramclone.domain.member.application.MemberService;
import com.example.instagramclone.domain.post.api.LikeStatusResponse;
import com.example.instagramclone.domain.post.domain.PostLikeRepository;
import com.example.instagramclone.domain.post.domain.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * [Day 12 Step 2] 좋아요 토글 API 로직.
 *
 * 라이브 코딩에서 구현할 내용:
 * - 이미 좋아요 했으면 삭제(취소), 없으면 추가(Insert)
 * - [Day 12 Part 2] Post.likeCount 비정규화: 추가 시 +1, 취소 시 -1
 * - 응답: LikeStatusResponse(liked, likeCount)
 */
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;

    /**
     * [Day 12 Step 2] TODO: 좋아요 토글 로직 구현
     * - postId로 Post 조회 (없으면 예외)
     * - loginMemberId로 Member 참조 (getReferenceById)
     * - existsByMemberAndPost → true면 deleteByMemberAndPost 후 liked=false, likeCount 갱신
     * - false면 PostLike.create(member, post) 저장 후 liked=true, likeCount 갱신
     * - [Day 12 Part 2] Post.likeCount 필드 +1 / -1 반영
     */
    @Transactional
    public LikeStatusResponse toggleLike(Long loginMemberId, Long postId) {
        // TODO [Day 12 Step 2]: 위 주석대로 토글 로직 구현 후 LikeStatusResponse(liked, likeCount) 반환
        return LikeStatusResponse.empty();
    }
}
