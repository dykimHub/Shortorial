package com.sleep.sleep.shorts.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TriedShortsDto {
    private int triedShortsId; // 시도한 쇼츠 테이블 id
    private OffsetDateTime triedShortsDate; // 시도한 시간
    @With
    private ShortsDto shortsDto; // 쇼츠 정보

    @QueryProjection
    @Builder
    public TriedShortsDto(int triedShortsId, OffsetDateTime triedShortsDate, ShortsDto shortsDto) {
        this.triedShortsId = triedShortsId;
        this.triedShortsDate = triedShortsDate;
        this.shortsDto = shortsDto;
    }

}
