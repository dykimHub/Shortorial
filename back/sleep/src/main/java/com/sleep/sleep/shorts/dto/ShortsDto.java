package com.sleep.sleep.shorts.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortsDto {
    // 쇼츠 테이블 id
    private int shortsId;
    // 쇼츠 시간
    private int shortsTime;
    // 쇼츠 제목
    private String shortsTitle;
    // 쇼츠 음악
    private String shortsMusicTitle;
    // 쇼츠 가수
    private String shortsMusicSinger;
    // 쇼츠 출처
    private String shortsSource;
    // 쇼츠 S3 key
    private String shortsS3Key;
    // 쇼츠 S3 PresingedGetURL
    @Setter
    private String shortsS3URL;
    // 쇼츠 시도한 사람 수
    private int shortsChallengerNum;

    @QueryProjection
    public ShortsDto(int shortsId, int shortsTime, String shortsTitle, String shortsMusicTitle, String shortsMusicSinger, String shortsSource, int shortsChallengerNum) {
        this.shortsId = shortsId;
        this.shortsTime = shortsTime;
        this.shortsTitle = shortsTitle;
        this.shortsMusicTitle = shortsMusicTitle;
        this.shortsMusicSinger = shortsMusicSinger;
        this.shortsSource = shortsSource;
        this.shortsChallengerNum = shortsChallengerNum;
    }

    @Builder
    public ShortsDto(int shortsId, String shortsTitle, String shortsS3Key, String shortsS3URL) {
        this.shortsId = shortsId;
        this.shortsTitle = shortsTitle;
        this.shortsS3Key = shortsS3Key;
        this.shortsS3URL = shortsS3URL;
    }
}