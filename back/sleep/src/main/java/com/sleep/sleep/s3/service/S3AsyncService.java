package com.sleep.sleep.s3.service;

import com.sleep.sleep.s3.dto.S3ObjectDto;
import com.sleep.sleep.s3.dto.S3PutRequestDTO;

import java.time.Duration;
import java.util.List;

public interface S3AsyncService {
    String generatePresignedGetURL(String accessToken, String fileName);

    String generatePresignedGetURL(String s3key, Duration valid);

    List<S3ObjectDto> getRecordedShortsList(String accessToken);

    String generatePresignedPutURL(String accessToken, S3PutRequestDTO s3PutRequestDTO);

}
