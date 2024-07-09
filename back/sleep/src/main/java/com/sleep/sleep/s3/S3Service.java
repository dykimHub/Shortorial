package com.sleep.sleep.s3;

import com.amazonaws.services.s3.model.S3Object;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    S3Object findS3Object(String s3key);

    String findS3URL(String s3key);

    byte[] findBlobOfS3Object(String s3Key) throws IOException;

    String addRecordedShortsToS3(String accessToken, MultipartFile file) throws IOException;

    //void deleteFile(int uploadNo, String fileName);

    //File convertMultiPartFileToFile(MultipartFile multipartFile) throws IOException;

    //InputStream downloadFile(String filePath);

    //void reaname(int uploadNo, String oldTitle, String newTitle);
}
