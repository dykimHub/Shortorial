package com.sleep.sleep.shorts.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordedShortsDto {

    // 녹화한 쇼츠 테이블 Id
    private int recordedShortsId;

    // 녹화한 쇼츠 s3 링크
    private String recordedShortsLink;

    // 녹화한 쇼츠 제목
    private String recordedShortsTitle;

    // 녹화한 시간
    private LocalDateTime recordedShortsDate;

    // 유튜브 업로드 url
    private String recordedShortsYoutubeUrl;

    // 원본 쇼츠 음악
    private String shortsMusic;

    // 원본 쇼츠 가수
    private String shortsSigner;
}
