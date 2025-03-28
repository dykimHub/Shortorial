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

    @Operation(summary = "S3에서 객체를 조회할 수 있는 Presigned URL을 생성")
    @GetMapping("/get")
    public ResponseEntity<String> createPresignedGetURL(@RequestHeader("Authorization") String accessToken,
                                                        @RequestParam("s3Key") String s3Key) {
        String presignedGetURL = s3AsyncService.generatePresignedGetURL(accessToken, s3Key);
        return ResponseEntity.ok()
                .body(presignedGetURL);

    }

}