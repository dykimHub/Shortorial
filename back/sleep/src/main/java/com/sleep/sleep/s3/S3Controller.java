package com.sleep.sleep.s3;

import com.sleep.sleep.exception.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Slf4j
public class S3Controller {

    private final S3Service s3Service;

    @Operation(summary = "S3에 업로드된 쇼츠를 Blob 형태로 반환")
    @PostMapping("/blob")
    public ResponseEntity<byte[]> findBlobOfS3Object(@RequestHeader("Authorization") String accessToken, @RequestBody Map<String, String> map) throws IOException {
        byte[] byteArray = s3Service.findBlobOfS3Object(map.get("s3key"));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(byteArray);
    }

    @Operation(summary = "회원이 녹화한 쇼츠를 S3에 업로드")
    @PostMapping
    public ResponseEntity<String> addRecordedShortsToS3(@RequestHeader("Authorization") String accessToken, @RequestParam("file") MultipartFile file) throws IOException {
        String recordedShortsS3Key = s3Service.addRecordedShortsToS3(accessToken, file);
        return ResponseEntity.ok()
                .body(recordedShortsS3Key);
    }

    @Operation(summary = "회원이 녹화한 쇼츠를 S3에서 삭제")
    @DeleteMapping
    public ResponseEntity<SuccessResponse> deleteRecordedShortsFromS3(@RequestHeader("Authorization") String accessToken, @RequestBody Map<String, String> map) {
        System.out.println(map);
        SuccessResponse successResponse = s3Service.deleteRecordedShortsFromS3(map.get("s3key"));
        return ResponseEntity.ok()
                .body(successResponse);
    }

}