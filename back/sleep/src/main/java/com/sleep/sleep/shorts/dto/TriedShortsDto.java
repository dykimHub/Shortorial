package com.sleep.sleep.shorts.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TriedShortsDto {

    // 시도한 쇼츠 테이블 id
    private int triedShortsId;

    // 시도한 시간
    private LocalDateTime triedShortsDate;

    // 쇼츠 정보
    private ShortsDto shortsDto;

    @Builder
    public TriedShortsDto(int triedShortsId, LocalDateTime triedShortsDate, ShortsDto shortsDto) {
        this.triedShortsId = triedShortsId;
        this.triedShortsDate = triedShortsDate;
        this.shortsDto = shortsDto;
    }

}
