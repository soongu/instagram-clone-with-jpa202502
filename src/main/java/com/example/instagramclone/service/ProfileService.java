package com.example.instagramclone.service;

import com.example.instagramclone.domain.follow.dto.response.FollowStatusResponse;
import com.example.instagramclone.domain.member.dto.response.MemberWithStatsDto;
import com.example.instagramclone.domain.member.dto.response.MeResponse;
import com.example.instagramclone.domain.member.dto.response.ProfileHeaderResponse;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.domain.post.dto.response.ProfilePostResponse;
import com.example.instagramclone.exception.ErrorCode;
import com.example.instagramclone.exception.MemberException;
import com.example.instagramclone.repository.MemberRepository;
import com.example.instagramclone.repository.PostRepository;
import com.example.instagramclone.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

// 개인 프로필 처리
@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProfileService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;

    private final FileUploadUtil fileUploadUtil;

    /**
     * 로그인한 유저 정보를 반환하는 처리
     * @param username - 인증된 사용자 이름 (스프링 시큐리티에 의해 컨트롤러에서 받아옴)
     * @return 인증된 사용자의 프로필정보 (이름, 사용자계정, 프로필사진)
     */
    public MeResponse getLoggedInUser(String username) {
        Member foundMember = getMember(username);

        return MeResponse.from(foundMember);
    }

    // 프로필 페이지 상단 헤더에 사용할 데이터 가져오기
    public ProfileHeaderResponse getProfileHeader(String username, String loginUsername) {

        // 사용자 이름에 매칭되는 회원정보와 통계 데이터를 한 번에 조회
        MemberWithStatsDto memberWithStats = memberRepository.findMemberWithStats(username, loginUsername)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        return ProfileHeaderResponse.of(
                memberWithStats.getMember()
                , memberWithStats.getFeedCount()
                , FollowStatusResponse.of(
                        memberWithStats.isFollowing(),
                        memberWithStats.getFollowerCount(),
                        memberWithStats.getFollowingCount()
                )
        );
    }

    // 프로필 페이지 피드 목록에 사용할 데이터 처리
    public List<ProfilePostResponse> findProfilePosts(String username) {
        return postRepository.findProfilePosts(getMember(username).getId());

    }

    private Member getMember(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(
                        () -> new MemberException(ErrorCode.MEMBER_NOT_FOUND)
                );
    }

    // 프로필 사진 처리
    @Transactional
    public String updateProfileImage(MultipartFile profileImage, String username) {
        // 1. 프로필 사진 이미지 파일을 서버에 저장
        String uploadPath = fileUploadUtil.saveFile(profileImage);

        // 2. 데이터베이스 업데이트
        memberRepository.updateProfileImage(uploadPath, username);

        // 3. 업로드 경로를 리턴하여 화면에 바로 렌더링
        return uploadPath;
    }

}
