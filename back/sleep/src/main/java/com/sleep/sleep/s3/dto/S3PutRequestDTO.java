package com.sleep.sleep.s3.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class S3PutRequestDTO {
    int shortsId; // 원본 쇼츠 Id
    Map<String, String> metadata; // 인코딩된 원본 쇼츠 S3 key
}
