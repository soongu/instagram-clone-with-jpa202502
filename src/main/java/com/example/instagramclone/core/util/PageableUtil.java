package com.example.instagramclone.core.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 프론트엔드(1-based)의 페이지 요청을 백엔드(0-based) Spring Data JPA Pageable 객체로
 * 안전하게 변환하고 검증하는 유틸리티 클래스
 */
public class PageableUtil {

    private static final int MIN_PAGE = 1;
    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 50;

    /**
     * 프론트엔드에서 전달받은 page, size 파라미터를 검증 및 보정하여 최신순(id DESC) 정렬된 Pageable 반환
     */
    public static Pageable createSafePageableDesc(int page, int size, String sortProperty) {
        // 비정상적인 페이지 번호나 과도한 데이터 요청 방어
        int safePage = Math.max(page, MIN_PAGE);
        int safeSize = (size >= MIN_SIZE && size <= MAX_SIZE) ? size : 5; // 기본값 5로 보정

        // 프론트엔드 페이지는 1부터 시작하지만, Spring Data JPA는 0-index 기반이므로 1을 빼서 PageRequest 생성
        return PageRequest.of(safePage - 1, safeSize, Sort.by(Sort.Direction.DESC, sortProperty));
    }
}
