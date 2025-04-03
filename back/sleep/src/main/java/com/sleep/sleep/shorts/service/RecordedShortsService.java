package com.sleep.sleep.shorts.service;

import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.s3.dto.S3PutRequestDTO;
import com.sleep.sleep.s3.dto.S3PutResponseDTO;
import com.sleep.sleep.shorts.dto.ModifyingStatusDto;

public interface RecordedShortsService {
    S3PutResponseDTO addRecordedShorts(String accessToken, S3PutRequestDTO s3PutRequestDTO);

    SuccessResponse modifyRecordedShortsStatus(ModifyingStatusDto modifyingStatusDto);
}
