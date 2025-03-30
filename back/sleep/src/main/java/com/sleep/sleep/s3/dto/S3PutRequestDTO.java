package com.sleep.sleep.s3.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class S3PutRequestDTO {
    String fileName; // 파일 제목(녹화 시간)
    Map<String, String> metadata; // 인코딩된 원본 쇼츠 S3 key
}
