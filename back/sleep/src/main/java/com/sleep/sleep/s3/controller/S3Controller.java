package com.sleep.sleep.s3.controller;

import com.sleep.sleep.s3.dto.S3ObjectDto;
import com.sleep.sleep.s3.service.S3AsyncService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;


@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Slf4j
public class S3Controller {
    private final S3AsyncService s3AsyncService;

    @Operation(summary = "S3 key를 활용하여 Presigned GET URL을 생성")
    @GetMapping("/get")
    public ResponseEntity<String> generatePresignedGetURL(@RequestHeader("Authorization") String accessToken, @RequestParam("s3key") String s3key) {
        String presignedGetURL = s3AsyncService.generatePresignedGetURL(s3key, Duration.ofMinutes(10));
        return ResponseEntity.ok()
                .body(presignedGetURL);

    }

    @GetMapping("/get/list")
    public ResponseEntity<List<S3ObjectDto>> getRecordedShortsList(@RequestHeader("Authorization") String accessToken) {
        List<S3ObjectDto> s3ObjectDtoList = s3AsyncService.getRecordedShortsList(accessToken);
        return ResponseEntity.ok()
                .body(s3ObjectDtoList);

    }

}