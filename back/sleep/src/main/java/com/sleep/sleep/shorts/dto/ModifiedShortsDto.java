package com.sleep.sleep.shorts.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifiedShortsDto {
    int recordedShortsId;
    String newRecordedShortsTitle;
}
