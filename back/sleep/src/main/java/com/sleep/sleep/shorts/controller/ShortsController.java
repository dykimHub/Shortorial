package com.sleep.sleep.shorts.controller;

import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.s3.dto.S3PutRequestDTO;
import com.sleep.sleep.s3.dto.S3PutResponseDTO;
import com.sleep.sleep.shorts.dto.*;
import com.sleep.sleep.shorts.service.RecordedShortsService;
import com.sleep.sleep.shorts.service.ShortsService;
import com.sleep.sleep.shorts.service.TriedShortsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/shorts")
@RestController
public class ShortsController {
    private final ShortsService shortsService;
    private final TriedShortsService triedShortsService;
    private final RecordedShortsService recordedShortsService;

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("ok");
    }

    @Operation(summary = "특정 쇼츠 조회")
    @GetMapping("/{shortsId}")
    public ResponseEntity<ShortsDto> findShorts(@RequestHeader("Authorization") String accessToken, @PathVariable int shortsId) {
        ShortsDto shortsDto = shortsService.findShortsDto(shortsId);
        return ResponseEntity.ok()
                .body(shortsDto);

    }

    @Operation(summary = "쇼츠 목록 조회")
    @GetMapping
    public ResponseEntity<List<ShortsDto>> findShortList(@RequestHeader("Authorization") String accessToken) {
        List<ShortsDto> shortsDtoList = shortsService.findShortsList();
        return ResponseEntity.ok()
                .body(shortsDtoList);

    }

    @Operation(summary = "쇼츠를 인기순으로 조회")
    @GetMapping("/rank")
    public ResponseEntity<List<ShortsDto>> findPopularShorts() {
        List<ShortsDto> shortRankingList = shortsService.findPopularShortsList();
        return ResponseEntity.ok()
                .body(shortRankingList);

    }

    @Operation(summary = "회원 쇼츠 통계 조회")
    @GetMapping("/stats")
    public ResponseEntity<ShortsStatsDto> findShortsStats(@RequestHeader("Authorization") String accessToken) {
        ShortsStatsDto shortsStatsDto = shortsService.findShortsStats(accessToken);
        return ResponseEntity.ok()
                .body(shortsStatsDto);
    }

    @Operation(summary = "회원이 시도한 쇼츠 조회")
    @GetMapping("/tried")
    public ResponseEntity<List<TriedShortsDto>> findTriedShortsList(@RequestHeader("Authorization") String accessToken) {
        List<TriedShortsDto> shortsList = triedShortsService.findTriedShortsList(accessToken);
        return ResponseEntity.ok()
                .body(shortsList);

    }

    @Operation(summary = "회원이 시도한 쇼츠에 추가")
    @PostMapping("/tried/{shortsId}")
    public ResponseEntity<SuccessResponse> addTriedShorts(@RequestHeader("Authorization") String accessToken, @PathVariable int shortsId) {
        SuccessResponse successResponse = triedShortsService.addTriedShorts(accessToken, shortsId);
        return ResponseEntity.ok()
                .body(successResponse);

    }

    @Operation(summary = "회원이 시도한 쇼츠에서 삭제")
    @DeleteMapping("/tried/{shortsId}")
    public ResponseEntity<SuccessResponse> deleteTriedShorts(@RequestHeader("Authorization") String accessToken, @PathVariable int shortsId) {
        SuccessResponse successResponse = triedShortsService.deleteTriedShorts(accessToken, shortsId);
        return ResponseEntity.ok()
                .body(successResponse);
    }

    @Operation(summary = "회원이 녹화한 쇼츠에 추가")
    @PostMapping("/recorded")
    public ResponseEntity<S3PutResponseDTO> addRecordedShorts(@RequestHeader("Authorization") String accessToken,
                                                              @RequestBody S3PutRequestDTO s3PutRequestDTO) {
        S3PutResponseDTO s3PutResponseDTO = recordedShortsService.addRecordedShorts(accessToken, s3PutRequestDTO);
        return ResponseEntity.ok()
                .body(s3PutResponseDTO);
    }

    @Operation(summary = "회원이 녹화한 쇼츠의 S3 상태 변경")
    @PutMapping("/recorded")
    public ResponseEntity<SuccessResponse> modifyRecordedShortsStatus(@RequestHeader("Authorization") String accessToken,
                                                                      @RequestBody ModifyingStatusDto modifyingStatusDto) {
        SuccessResponse successResponse = recordedShortsService.modifyRecordedShortsStatus(accessToken, modifyingStatusDto);
        return ResponseEntity.ok()
                .body(successResponse);
    }

    @Operation(summary = "회원이 녹화한 쇼츠 제목 변경")
    @PutMapping("/recorded/title")
    public ResponseEntity<SuccessResponse> modifyRecordedShortsTitle(@RequestHeader("Authorization") String accessToken, @RequestBody ModifyingTitleDto modifyingTitleDto) {
        SuccessResponse successResponse = recordedShortsService.modifyRecordedShortsTitle(accessToken, modifyingTitleDto);
        return ResponseEntity.ok()
                .body(successResponse);
    }

    @Operation(summary = "회원이 녹화한 쇼츠 목록 조회")
    @GetMapping("/recorded")
    public ResponseEntity<List<RecordedShortsDto>> findRecordedShortsList(@RequestHeader("Authorization") String accessToken) {
        List<RecordedShortsDto> recordedShortsDtoList = recordedShortsService.findRecordedShortsDtoList(accessToken);
        return ResponseEntity.ok()
                .body(recordedShortsDtoList);
    }

    @Operation(summary = "회원이 녹화한 쇼츠 삭제")
    @DeleteMapping("/recorded/{recordedShortsId}")
    public ResponseEntity<SuccessResponse> deleteRecordedShorts(@RequestHeader("Authorization") String accessToken, @PathVariable int recordedShortsId) {
        SuccessResponse successResponse = recordedShortsService.deleteRecordedShorts(accessToken, recordedShortsId);
        return ResponseEntity.ok()
                .body(successResponse);
    }


}
