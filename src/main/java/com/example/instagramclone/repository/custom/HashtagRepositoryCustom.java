package com.example.instagramclone.repository.custom;

import com.example.instagramclone.domain.hashtag.dto.response.HashtagSearchResponse;

import java.util.List;

public interface HashtagRepositoryCustom {

    List<HashtagSearchResponse> searchHashtagsByKeyword(String keyword);
}
