package com.example.instagramclone.domain.comment.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentCreationResponse {

    private CommentResponse comment;
    private long commentCount;

    public static CommentCreationResponse of(CommentResponse comment, long commentCount) {
        return CommentCreationResponse.builder()
                .comment(comment)
                .commentCount(commentCount)
                .build();
    }
}
