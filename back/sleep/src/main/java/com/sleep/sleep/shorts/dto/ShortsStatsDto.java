package com.sleep.sleep.shorts.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortsStatsDto {
    int triedShortsNum; // 시도한 쇼츠 수
    int recordedShortsNum; // 녹화한 쇼츠 수
    String unRecordedShortsTitle; // 시도했는데 녹화하지 않은 쇼츠 제목

    @Builder
    public ShortsStatsDto(int triedShortsNum, int recordedShortsNum, String unRecordedShortsTitle) {
        this.triedShortsNum = triedShortsNum;
        this.recordedShortsNum = recordedShortsNum;
        this.unRecordedShortsTitle = unRecordedShortsTitle;
    }
}
