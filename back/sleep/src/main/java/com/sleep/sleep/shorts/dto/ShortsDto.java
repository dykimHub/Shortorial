package com.sleep.sleep.shorts.dto;

import com.querydsl.core.annotations.QueryProjection;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;

@With
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

    // 쇼츠 s3 링크
    private String shortsS3Link;

    // 쇼츠 시도한 사람 수
    private int shortsChallengerNum;

    @Builder
    public ShortsDto(int shortsId, int shortsTime, String shortsTitle, String shortsMusicTitle, String shortsMusicSinger, String shortsSource, String shortsS3Link, int shortsChallengerNum) {
        this.shortsId = shortsId;
        this.shortsTime = shortsTime;
        this.shortsTitle = shortsTitle;
        this.shortsMusicTitle = shortsMusicTitle;
        this.shortsMusicSinger = shortsMusicSinger;
        this.shortsSource = shortsSource;
        this.shortsS3Link = shortsS3Link;
        this.shortsChallengerNum = shortsChallengerNum;
    }

    // shortsS3Link를 매개변수로 받지 않는 생성자
    @QueryProjection
    @Builder
    public ShortsDto(int shortsId, int shortsTime, String shortsTitle, String shortsMusicTitle, String shortsMusicSinger, String shortsSource, int shortsChallengerNum) {
        this.shortsId = shortsId;
        this.shortsTime = shortsTime;
        this.shortsTitle = shortsTitle;
        this.shortsMusicTitle = shortsMusicTitle;
        this.shortsMusicSinger = shortsMusicSinger;
        this.shortsSource = shortsSource;
        this.shortsChallengerNum = shortsChallengerNum;
    }
}