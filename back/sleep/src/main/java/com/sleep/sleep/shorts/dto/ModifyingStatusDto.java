package com.sleep.sleep.shorts.dto;

import com.sleep.sleep.s3.constants.S3Status;
import lombok.Getter;

@Getter
public class ModifyingStatusDto {
    private String recordedShortsS3key;
    private S3Status status;
}
