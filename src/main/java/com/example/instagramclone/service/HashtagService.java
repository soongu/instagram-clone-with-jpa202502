package com.example.instagramclone.service;

import com.example.instagramclone.domain.hashtag.dto.response.HashtagSearchResponse;
import com.example.instagramclone.domain.post.dto.response.FeedResponse;
import com.example.instagramclone.domain.post.dto.response.ProfilePostResponse;

import com.example.instagramclone.repository.HashtagRepository;
import com.example.instagramclone.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final PostRepository postRepository;

    // 해시태그 추천 목록 불러오기 중간처리
    @Transactional(readOnly = true)
    public List<HashtagSearchResponse> searchHashtags(String keyword) {

        // 검색어 전처리: # 제거 (Validation은 Controller에서 처리됨)
        String processedKeyword = keyword.startsWith("#") ? keyword.substring(1) : keyword;

        return hashtagRepository.searchHashtagsByKeyword(processedKeyword);
    }

    // 해시태그 피드 목록 불러오기
    @Transactional(readOnly = true)
    public FeedResponse<ProfilePostResponse> getPostsByHashtag(String tagName, int page, int size) {

        int offset = (page - 1) * size;

        List<ProfilePostResponse> hashtagFeedList
                = postRepository.findPostsByHashtag(tagName, offset, size + 1);

        // 다음페이지가 있는지 여부
        boolean hasNext = hashtagFeedList.size() > size;
        if (hasNext) {
            hashtagFeedList.remove(hashtagFeedList.size() - 1);
        }

        return FeedResponse.of(hashtagFeedList, hasNext);
    }

}
