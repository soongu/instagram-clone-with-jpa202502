package com.example.instagramclone.service;

import com.example.instagramclone.domain.member.dto.response.SuggestedMemberResponse;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.exception.ErrorCode;
import com.example.instagramclone.exception.MemberException;
import com.example.instagramclone.repository.MemberRepository;
import com.example.instagramclone.repository.SuggestionJdbcRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SuggestionService {

    private final MemberRepository memberRepository;
    private final SuggestionJdbcRepository suggestionJdbcRepository;

    /**
     * 추천 사용자 목록 조회
     * - 팔로우하지 않은 사용자 중에서
     * - 최근 가입했거나 활동이 많은 사용자를 추천
     */
    public List<SuggestedMemberResponse> getSuggestedMembers(String username, int size) {
        Member currentMember = findByUsername(username);
        return suggestionJdbcRepository.findSuggestedMembers(currentMember.getId(), size);
    }



    public Member findByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));
    }


}
