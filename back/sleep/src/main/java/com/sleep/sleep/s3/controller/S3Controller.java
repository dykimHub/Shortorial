package com.sleep.sleep.s3.controller;

import com.sleep.sleep.s3.dto.S3ObjectDto;
import com.sleep.sleep.s3.dto.S3PutRequestDTO;
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

    @Operation(summary = "S3 key를 활용하여 Presigned GET URL을 생성")
    @GetMapping("/get")
    public ResponseEntity<String> generatePresignedGetURL(@RequestHeader("Authorization") String accessToken,
                                                          @RequestParam("fileName") String fileName) {
        String presignedGetURL = s3AsyncService.generatePresignedGetURL(accessToken, fileName);
        return ResponseEntity.ok()
                .body(presignedGetURL);

    }

    @GetMapping("/get/list")
    public ResponseEntity<List<S3ObjectDto>> getRecordedShortsList(@RequestHeader("Authorization") String accessToken) {
        List<S3ObjectDto> s3ObjectDtoList = s3AsyncService.getRecordedShortsList(accessToken);
        return ResponseEntity.ok()
                .body(s3ObjectDtoList);

    }

    @Operation(summary = "S3에 객체를 업로드 할 수 있는 Presigned PUT URL을 생성")
    @PostMapping("/put")
    public ResponseEntity<String> generatePresignedPutURL(@RequestHeader("Authorization") String accessToken,
                                                          @RequestBody S3PutRequestDTO s3PutRequestDTO) {
        String presignedPutURL = s3AsyncService.generatePresignedPutURL(accessToken, s3PutRequestDTO);
        return ResponseEntity.ok()
                .body(presignedPutURL);

    }

}