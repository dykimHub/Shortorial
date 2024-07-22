package com.sleep.sleep.s3;

import com.amazonaws.services.s3.model.S3Object;
import com.sleep.sleep.exception.SuccessResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    S3Object findS3Object(String s3key);

    String findS3URL(String s3key);

    byte[] findBlobOfS3Object(String s3Key) throws IOException;

    String addRecordedShortsToS3(String accessToken, MultipartFile file) throws IOException;

    SuccessResponse deleteRecordedShortsFromS3(String s3key);
}
