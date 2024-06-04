package com.sleep.sleep.shorts.dto;

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

    // 쇼츠 챌린저 수
    private int shortsChallengersNum;

    // 쇼츠 s3 링크
    private String shortsLink;

}