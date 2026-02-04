package com.example.instagramclone.service;

import com.example.instagramclone.domain.member.dto.response.MemberSearchResponse;
import com.example.instagramclone.domain.member.entity.Member;
import com.example.instagramclone.exception.ErrorCode;
import com.example.instagramclone.exception.MemberException;
import com.example.instagramclone.repository.MemberRepository;
import com.example.instagramclone.repository.SearchJdbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
public class SearchService {

    private final MemberRepository memberRepository;
    private final SearchJdbcRepository searchJdbcRepository;

    // 사용자 검색 기능
    public List<MemberSearchResponse> searchMembers(String keyword, String username) {

        // 로그인한 회원 조회
        Member currentMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberException(ErrorCode.MEMBER_NOT_FOUND));

        // 검색어 기반 조회 (Native Query + JdbcTemplate)
        return searchJdbcRepository.searchMembers(keyword, currentMember.getId(), 5);
    }
}
