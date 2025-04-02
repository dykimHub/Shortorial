package com.sleep.sleep.s3.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class S3PutResponseDTO {
    private String processedShortsS3key;
    private String presignedPutURL;

    @Builder
    public S3PutResponseDTO(String processedShortsS3key, String presignedPutURL) {
        this.processedShortsS3key = processedShortsS3key;
        this.presignedPutURL = presignedPutURL;
    }
}
