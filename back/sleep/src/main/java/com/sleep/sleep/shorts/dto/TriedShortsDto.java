package com.sleep.sleep.shorts.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TriedShortsDto {

    // 시도한 쇼츠 테이블 id
    private int tryShortsId;

    // 시도한 시간
    private LocalDateTime triedShortsDate;

    // 원본 쇼츠 음악
    private String shortsMusicTitle;

    // 원본 쇼츠 가수
    private String shortsMusicSigner;

    // 쇼츠 시간
    private int shortsTime;

    // 쇼츠 제목
    private String shortsTitle;

    // 쇼츠 챌린저 수
    private int shortsChallengersNum;

    // 쇼츠 s3 링크
    private String shortsS3Link;

    @Builder
    public TriedShortsDto(int tryShortsId, LocalDateTime triedShortsDate, String shortsMusicTitle, String shortsMusicSigner, int shortsTime, String shortsTitle, int shortsChallengersNum, String shortsS3Link) {
        this.tryShortsId = tryShortsId;
        this.triedShortsDate = triedShortsDate;
        this.shortsMusicTitle = shortsMusicTitle;
        this.shortsMusicSigner = shortsMusicSigner;
        this.shortsTime = shortsTime;
        this.shortsTitle = shortsTitle;
        this.shortsChallengersNum = shortsChallengersNum;
        this.shortsS3Link = shortsS3Link;
    }
}
