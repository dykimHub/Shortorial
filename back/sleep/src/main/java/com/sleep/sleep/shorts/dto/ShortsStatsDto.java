package com.sleep.sleep.shorts.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ShortsStatsDto {

    int triedShortsNum;
    int recordedShortsNum;
    int uploadedShortsNum;

    // Dto를 QClass로 만들어서 QueryDSL에서 객체 생성이 가능하게 함
    // @QueryProjection
    @Builder
    public ShortsStatsDto(int triedShortsNum, int recordedShortsNum, int uploadedShortsNum) {
        this.triedShortsNum = triedShortsNum;
        this.recordedShortsNum = recordedShortsNum;
        this.uploadedShortsNum = uploadedShortsNum;
    }
}
