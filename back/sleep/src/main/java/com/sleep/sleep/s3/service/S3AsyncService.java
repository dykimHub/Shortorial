package com.sleep.sleep.s3.service;

import com.sleep.sleep.s3.dto.S3ObjectDto;
import com.sleep.sleep.s3.dto.S3PutResponseDTO;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public interface S3AsyncService {
    String generatePresignedGetURL(String s3key, Duration valid);

    List<S3ObjectDto> getRecordedShortsList(String accessToken);

    String generatePresignedPutURL(String s3key, Map<String, String> metadata, Duration valid);

}
