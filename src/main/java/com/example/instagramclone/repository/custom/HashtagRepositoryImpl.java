package com.example.instagramclone.repository.custom;

import com.example.instagramclone.domain.hashtag.dto.response.HashtagSearchResponse;
import com.example.instagramclone.domain.hashtag.entity.QHashtag;
import com.example.instagramclone.domain.hashtag.entity.QPostHashtag;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class HashtagRepositoryImpl implements HashtagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<HashtagSearchResponse> searchHashtagsByKeyword(String keyword) {
        QHashtag hashtag = QHashtag.hashtag;
        QPostHashtag postHashtag = QPostHashtag.postHashtag;

        return queryFactory
                .select(Projections.constructor(HashtagSearchResponse.class,
                        hashtag.name.as("hashtag"),
                        postHashtag.count().intValue().as("feedCount")))
                .from(hashtag)
                .leftJoin(postHashtag)
                .on(hashtag.id.eq(postHashtag.hashtag.id))
                .where(hashtag.name.startsWith(keyword))
                .groupBy(hashtag.name)
                .orderBy(postHashtag.count().desc())
                .limit(5)
                .fetch();
    }
}
