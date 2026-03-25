package com.example.instagramclone.domain.post.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class PostLikeRepositoryCustomImpl implements PostLikeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Map<Long, Long> countLikesByPostIds(List<Long> postIds) {
        // TODO: (Day 15) IN 쿼리를 사용하여 postIds 에 해당하는 게시글들의 좋아요 수를 배치로 집계하세요.
        // QueryDSL 의 groupBy와 transform 구문을 활용하면 Map 으로 바로 반환할 수 있습니다.
        return Collections.emptyMap();
    }
}
