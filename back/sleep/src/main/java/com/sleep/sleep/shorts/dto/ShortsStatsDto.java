package com.sleep.sleep.shorts.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortsStatsDto {
    // 시도한 쇼츠 수
    int triedShortsNum;
    // 녹화한 쇼츠 수
    int recordedShortsNum;
    // 유튜브에 업로드한 쇼츠 수
    int uploadedShortsNum;

    @QueryProjection
    @Builder
    public ShortsStatsDto(int triedShortsNum, int recordedShortsNum, int uploadedShortsNum) {
        this.triedShortsNum = triedShortsNum;
        this.recordedShortsNum = recordedShortsNum;
        this.uploadedShortsNum = uploadedShortsNum;
    }
}
