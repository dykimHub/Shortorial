package com.sleep.sleep.shorts.dto;

import lombok.*;

import java.time.OffsetDateTime;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecordedShortsDto {

    // 녹화한 쇼츠 테이블 Id
    private int recordedShortsId;

    // 녹화한 쇼츠 제목
    private String recordedShortsTitle;

    // 녹화한 시간
    private OffsetDateTime recordedShortsDate;

    // 녹화한 쇼츠 S3 key
    private String recordedShortsS3key;

    // 녹화한 쇼츠 S3 URL
    private String recordedShortsS3URL;

    // 유튜브 업로드 URL
    private String recordedShortsYoutubeURL;

    @Builder
    public RecordedShortsDto(int recordedShortsId, String recordedShortsTitle, OffsetDateTime recordedShortsDate, String recordedShortsS3key, String recordedShortsS3URL, String recordedShortsYoutubeURL) {
        this.recordedShortsId = recordedShortsId;
        this.recordedShortsTitle = recordedShortsTitle;
        this.recordedShortsDate = recordedShortsDate;
        this.recordedShortsS3key = recordedShortsS3key;
        this.recordedShortsS3URL = recordedShortsS3URL;
        this.recordedShortsYoutubeURL = recordedShortsYoutubeURL;
    }
}
