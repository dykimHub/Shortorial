package com.sleep.sleep.shorts.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortsStatsDto {

    int triedShortsNum;
    int recordedShortsNum;
    int uploadedShortsNum;

    // Dto를 QClass로 만들어서 QueryDSL에서 객체 생성이 가능하게 함
    @QueryProjection
    public ShortsStatsDto(int triedShortsNum, int recordedShortsNum, int uploadedShortsNum) {
        this.triedShortsNum = triedShortsNum;
        this.recordedShortsNum = recordedShortsNum;
        this.uploadedShortsNum = uploadedShortsNum;
    }
}
