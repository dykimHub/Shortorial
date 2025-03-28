package com.sleep.sleep.s3;

import com.sleep.sleep.s3.dto.S3ObjectDto;
import com.sleep.sleep.s3.service.S3AsyncService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Slf4j
public class S3Controller {
    private final S3AsyncService s3AsyncService;

    @Operation(summary = "S3에서 객체를 조회할 수 있는 Presigned GET URL을 생성")
    @GetMapping("/get")
    public ResponseEntity<String> generatePresignedGetURL(@RequestHeader("Authorization") String accessToken,
                                                          @RequestParam("s3key") String s3key) {
        String presignedGetURL = s3AsyncService.generatePresignedGetURL(accessToken, s3key);
        return ResponseEntity.ok()
                .body(presignedGetURL);

    }

    @Operation(summary = "S3에 객체를 업로드 할 수 있는 Presigned PUT URL을 생성")
    @GetMapping("/put")
    public ResponseEntity<String> generatePresignedPutURL(@RequestHeader("Authorization") String accessToken,
                                                          @RequestParam("createdAt") String createdAt,
                                                          @RequestParam("originS3key") String originS3key) {
        String presignedURL = s3AsyncService.generatePresignedPutURL(accessToken, createdAt, originS3key);
        return ResponseEntity.ok()
                .body(presignedURL);

    }

    @GetMapping
    public ResponseEntity<List<S3ObjectDto>> getRecordedShortsList(@RequestHeader("Authorization") String accessToken) {
        List<S3ObjectDto> s3ObjectDtoList = s3AsyncService.getRecordedShortsList(accessToken);
        return ResponseEntity.ok()
                .body(s3ObjectDtoList);

    }

}