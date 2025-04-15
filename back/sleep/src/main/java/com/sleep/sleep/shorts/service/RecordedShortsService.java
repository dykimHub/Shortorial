package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.s3.dto.S3PutRequestDTO;
import com.sleep.sleep.s3.dto.S3PutResponseDTO;
import com.sleep.sleep.shorts.dto.ModifyingStatusDto;
import com.sleep.sleep.shorts.dto.ModifyingTitleDto;
import com.sleep.sleep.shorts.dto.RecordedShortsDto;

import java.util.List;

public interface RecordedShortsService {
    S3PutResponseDTO addRecordedShorts(String accessToken, S3PutRequestDTO s3PutRequestDTO);

    SuccessResponse modifyRecordedShortsStatus(String accessToken, ModifyingStatusDto modifyingStatusDto);

    SuccessResponse modifyRecordedShortsTitle(String accessToken, ModifyingTitleDto modifyingTitleDto);

    List<RecordedShortsDto> findRecordedShortsDtoList(String accessToken);

    SuccessResponse deleteRecordedShorts(String accessToken, int recordedShortsId);

}
