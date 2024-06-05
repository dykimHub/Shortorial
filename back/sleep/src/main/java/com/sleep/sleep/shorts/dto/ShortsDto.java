package com.sleep.sleep.shorts.dto;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;

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
    private String shortsMusic;

    // 쇼츠 가수
    private String shortsSinger;

    // 쇼츠 챌린저 수
    private int shortsChallengersNum;

    // 쇼츠 s3 링크
    private String shortsLink;

    @Builder
    public ShortsDto(int shortsId, int shortsTime, String shortsTitle, String shortsMusic, String shortsSinger, int shortsChallengersNum, String shortsLink) {
        this.shortsId = shortsId;
        this.shortsTime = shortsTime;
        this.shortsTitle = shortsTitle;
        this.shortsMusic = shortsMusic;
        this.shortsSinger = shortsSinger;
        this.shortsChallengersNum = shortsChallengersNum;
        this.shortsLink = shortsLink;
    }
}