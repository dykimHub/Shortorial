package com.sleep.sleep.s3.service;

import com.sleep.sleep.s3.dto.S3ObjectDto;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface S3AsyncService {
    String generatePresignedGetURL(String accessToken, String s3key);

    String generatePresignedPutURL(String accessToken, String createdAt, String originS3key);

    List<S3ObjectDto> getRecordedShortsList(String accessToken);
}
