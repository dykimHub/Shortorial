package com.sleep.sleep.shorts.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ModifyingShortsDto {
    int recordedShortsId;
    String newRecordedShortsTitle;
}
