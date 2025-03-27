package com.sleep.sleep.shorts.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.OffsetDateTime;

@With
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TriedShortsDto {

    // 시도한 쇼츠 테이블 id
    private int triedShortsId;
    // 시도한 시간
    private OffsetDateTime triedShortsDate;
    // 쇼츠 정보
    private ShortsDto shortsDto;

    @QueryProjection
    @Builder
    public TriedShortsDto(int triedShortsId, OffsetDateTime triedShortsDate, ShortsDto shortsDto) {
        this.triedShortsId = triedShortsId;
        this.triedShortsDate = triedShortsDate;
        this.shortsDto = shortsDto;
    }

}
