package com.sleep.sleep.shorts.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordedShortsDto {
    private int recordedShortsId; // 녹화한 쇼츠 테이블 Id
    private String recordedShortsTitle; // 녹화한 쇼츠 제목
    private OffsetDateTime recordedShortsDate; // 녹화한 시간
    private String recordedShortsS3key; // 녹화한 쇼츠 S3 key
    @With
    private String recordedShortsS3URL; // 녹화한 쇼츠 S3 URL
    private String recordedShortsYoutubeURL; // 유튜브 업로드 URL
    private String shortsMusicTitle; // 쇼츠 음악
    private String shortsMusicSinger; // 쇼츠 가수

    @QueryProjection
    public RecordedShortsDto(int recordedShortsId, String recordedShortsTitle, OffsetDateTime recordedShortsDate, String recordedShortsS3key, String recordedShortsYoutubeURL, String shortsMusicTitle, String shortsMusicSinger) {
        this.recordedShortsId = recordedShortsId;
        this.recordedShortsTitle = recordedShortsTitle;
        this.recordedShortsDate = recordedShortsDate;
        this.recordedShortsS3key = recordedShortsS3key;
        this.recordedShortsYoutubeURL = recordedShortsYoutubeURL;
        this.shortsMusicTitle = shortsMusicTitle;
        this.shortsMusicSinger = shortsMusicSinger;
    }

    @Builder
    public RecordedShortsDto(int recordedShortsId, String recordedShortsTitle, OffsetDateTime recordedShortsDate, String recordedShortsS3key, String recordedShortsS3URL, String recordedShortsYoutubeURL, String shortsMusicTitle, String shortsMusicSinger) {
        this.recordedShortsId = recordedShortsId;
        this.recordedShortsTitle = recordedShortsTitle;
        this.recordedShortsDate = recordedShortsDate;
        this.recordedShortsS3key = recordedShortsS3key;
        this.recordedShortsS3URL = recordedShortsS3URL;
        this.recordedShortsYoutubeURL = recordedShortsYoutubeURL;
        this.shortsMusicTitle = shortsMusicTitle;
        this.shortsMusicSinger = shortsMusicSinger;
    }
}
