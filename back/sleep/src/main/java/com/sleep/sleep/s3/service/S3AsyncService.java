package com.sleep.sleep.s3.service;

public interface S3AsyncService {
    String generatePresignedGetURL(String accessToken, String s3key);

    String generatePresignedPutURL(String accessToken, String createdAt, String originS3key);
}
