package com.sleep.sleep.shorts.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordedShortsDto {

    // 녹화한 쇼츠 테이블 Id
    private int recordedShortsId;

    // 녹화한 쇼츠 제목
    private String recordedShortsTitle;

    // 녹화한 시간
    private OffsetDateTime recordedShortsDate;

    // 녹화한 쇼츠 s3 링크
    private String recordedShortsS3Link;

    // 유튜브 업로드 url
    private String recordedShortsYoutubeUrl;

    @Builder
    public RecordedShortsDto(int recordedShortsId, String recordedShortsS3Link, String recordedShortsTitle, OffsetDateTime recordedShortsDate, String recordedShortsYoutubeURL) {
        this.recordedShortsId = recordedShortsId;
        this.recordedShortsS3Link = recordedShortsS3Link;
        this.recordedShortsTitle = recordedShortsTitle;
        this.recordedShortsDate = recordedShortsDate;
        this.recordedShortsYoutubeUrl = recordedShortsYoutubeURL;
    }
}
