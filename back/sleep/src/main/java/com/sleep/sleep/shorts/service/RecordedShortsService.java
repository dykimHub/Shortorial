package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.shorts.dto.ModifiedShortsDto;
import com.sleep.sleep.shorts.dto.RecordedShortsDto;

import java.util.List;

public interface RecordedShortsService {
    List<RecordedShortsDto> findRecordedShortsList(String accessToken);

    SuccessResponse addRecordedShorts(String accessToken, String recordedShortsS3Key);

    SuccessResponse modifyRecordedShortsTitle(String accessToken, ModifiedShortsDto modifiedShortsDto);

    SuccessResponse deleteRecordedShorts(String S3key);


}
