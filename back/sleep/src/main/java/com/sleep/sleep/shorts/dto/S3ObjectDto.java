package com.sleep.sleep.shorts.dto;

import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class S3ObjectDto {
    // S3 객체 키
    private String key;
    // S3 객체 URL
    private String url;
    // S3 객체
    private long size;
    // S3 객체 마지막 수정 날짜(UTC 기준)
    private Instant lastModified;

    @Builder
    public S3ObjectDto(String key, String url, long size, Instant lastModified) {
        this.key = key;
        this.url = url;
        this.size = size;
        this.lastModified = lastModified;
    }
}
