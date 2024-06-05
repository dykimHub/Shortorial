package com.sleep.sleep.shorts.dto;

import lombok.AccessLevel;
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
    private String shortsMusic;

    // 원본 쇼츠 가수
    private String shortsSigner;

}