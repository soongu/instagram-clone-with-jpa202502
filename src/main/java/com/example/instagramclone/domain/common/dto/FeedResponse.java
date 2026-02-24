package com.example.instagramclone.domain.common.dto;

import java.util.List;

// 무한 스크롤 및 목록 응답을 감싸는 래퍼 DTO를 작성하세요.
public record FeedResponse<T>(
        boolean hasNext,
        List<T> feedList
) {
    public static <T> FeedResponse<T> of(boolean hasNext, List<T> feedList) {
        return new FeedResponse<>(hasNext, feedList);
    }
}
