package com.example.instagramclone.domain.hashtag.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HashtagSearchResponse {
    private String hashtag;
    private int feedCount;
}
