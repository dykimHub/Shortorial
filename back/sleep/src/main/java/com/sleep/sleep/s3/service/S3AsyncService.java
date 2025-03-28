package com.sleep.sleep.s3.service;

public interface S3AsyncService {
    String generatePresignedGetURL(String accessToken, String s3Key);
}
