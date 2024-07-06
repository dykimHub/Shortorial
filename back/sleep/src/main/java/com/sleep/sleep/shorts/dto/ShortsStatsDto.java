package com.sleep.sleep.shorts.dto;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortsStatsDto {

    int triedShortsNum;
    int recordedShortsNum;
    int uploadedShortsNum;

    // @QueryProjection
    @Builder
    public ShortsStatsDto(int triedShortsNum, int recordedShortsNum, int uploadedShortsNum) {
        this.triedShortsNum = triedShortsNum;
        this.recordedShortsNum = recordedShortsNum;
        this.uploadedShortsNum = uploadedShortsNum;
    }
}
