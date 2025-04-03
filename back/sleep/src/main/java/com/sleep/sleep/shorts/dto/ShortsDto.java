package com.sleep.sleep.shorts.dto;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.transaction.Transactional;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortsDto {
    private int shortsId; // 쇼츠 테이블 id
    private int shortsTime; // 쇼츠 시간
    private String shortsTitle; // 쇼츠 제목
    private String shortsMusicTitle; // 쇼츠 음악
    private String shortsMusicSinger; // 쇼츠 가수
    private String shortsSource; // 쇼츠 출처
    private String shortsS3key; // 쇼츠 S3 key
    @With
    private String shortsS3URL; // 쇼츠 S3 PresingedGetURL
    private int shortsChallengerNum; // 쇼츠 시도한 사람 수

    @QueryProjection
    public ShortsDto(int shortsId, int shortsTime, String shortsTitle, String shortsMusicTitle, String shortsMusicSinger, String shortsSource, String shortsS3Key, int shortsChallengerNum) {
        this.shortsId = shortsId;
        this.shortsTime = shortsTime;
        this.shortsTitle = shortsTitle;
        this.shortsMusicTitle = shortsMusicTitle;
        this.shortsMusicSinger = shortsMusicSinger;
        this.shortsSource = shortsSource;
        this.shortsS3key = shortsS3Key;
        this.shortsChallengerNum = shortsChallengerNum;
    }

    @Builder
    public ShortsDto(int shortsId, int shortsTime, String shortsTitle, String shortsMusicTitle, String shortsMusicSinger, String shortsSource, String shortsS3Key, String shortsS3URL, int shortsChallengerNum) {
        this.shortsId = shortsId;
        this.shortsTime = shortsTime;
        this.shortsTitle = shortsTitle;
        this.shortsMusicTitle = shortsMusicTitle;
        this.shortsMusicSinger = shortsMusicSinger;
        this.shortsSource = shortsSource;
        this.shortsS3key = shortsS3Key;
        this.shortsS3URL = shortsS3URL;
        this.shortsChallengerNum = shortsChallengerNum;
    }
}