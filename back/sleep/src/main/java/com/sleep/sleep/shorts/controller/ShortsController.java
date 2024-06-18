package com.sleep.sleep.shorts.controller;

import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.shorts.dto.RecordedShortsDto;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import com.sleep.sleep.shorts.service.ShortsService;
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

    @Operation(summary = "쇼츠 목록 조회")
    @GetMapping
    public ResponseEntity<List<ShortsDto>> findShortList() {
        List<ShortsDto> shortsDtoList = shortsService.findShortsList();
        return ResponseEntity.ok()
                .body(shortsDtoList);

    }

    @Operation(summary = "특정 쇼츠 조회")
    @GetMapping("/{shortsId}")
    public ResponseEntity<ShortsDto> findShorts(@PathVariable int shortsId) {
        ShortsDto shortsDto = shortsService.findShorts(shortsId);
        return ResponseEntity.ok()
                .body(shortsDto);

    }

    @Operation(summary = "쇼츠를 인기순으로 조회")
    @GetMapping("/ranking")
    public ResponseEntity<?> findPopularShorts() {
        List<ShortsDto> shortRankingList = shortsService.findPopularShortsList();
        return ResponseEntity.ok()
                .body(shortRankingList);

    }

    @Operation(summary = "회원이 녹화한 영상 조회")
    @GetMapping("/recorded")
    public ResponseEntity<List<RecordedShortsDto>> findRecordedShortsList(@RequestHeader("Authorization") String accessToken) {
        List<RecordedShortsDto> recordedShortsDtoList = shortsService.findRecordedShortsList(accessToken);
        return ResponseEntity.ok()
                .body(recordedShortsDtoList);

    }

    @Operation(summary = "회원이 시도한 영상 조회")
    @GetMapping("/tried")
    public ResponseEntity<List<TriedShortsDto>> findTriedshortsList(@RequestHeader("Authorization") String accessToken) {
        List<TriedShortsDto> shortsList = shortsService.findTriedShortsList(accessToken);
        return ResponseEntity.ok()
                .body(shortsList);

    }

    @Operation(summary = "회원이 시도한 영상에 추가")
    @PostMapping("/tried/{shortsId}")
    public ResponseEntity<SuccessResponse> addTriedShorts(@RequestHeader("Authorization") String accessToken, @PathVariable int shortsId) {
        SuccessResponse successResponse = shortsService.addTriedShorts(accessToken, shortsId);
        return ResponseEntity.ok()
                .body(successResponse);

    }

//    @Operation(summary = "회원이 시도한 영상에 삭제")
//    @DeleteMapping("/tried/{shortsId}")
//    public ResponseEntity<SuccessResponse> deleteTriedShorts(@RequestHeader("Authorization") String accessToken, @PathVariable int shortsId) {
//        SuccessResponse successResponse = shortsService.deleteTriedShorts(accessToken, shortsId);
//        return ResponseEntity.ok()
//                .body(successResponse);
//
//    }

//    @Operation(summary = "동영상 파일 이름 중복검사", description = "헤더에 accessToken 넣기, RequestParam으로 title 받기. true면 이미 있는 이름; false면 사용 가능한 이름 ")
//    @GetMapping("/checkName")
//    public ResponseEntity<?> checkName(@RequestHeader("Authorization") String accessToken, @RequestBody Map<String, String> data) {
//        try {
//            String title = data.get("title");
//
//            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
//            System.out.println("username : "+ username);
//
//            Boolean possible = shortsService.isNameExists(username+"/"+title);
//
//            if (possible) {
//                log.info("사용 가능 여부 : "+ possible);
//                return new ResponseEntity<>("Name is available.", HttpStatus.OK);
//            } else {
//                return new ResponseEntity<>("Name already exists. Please choose another one.", HttpStatus.BAD_REQUEST);
//            }
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//    @Operation(summary = "시도한 영상 카운트 올리기", description = "헤더에 accessToken 넣기, RequestParam으로 shortsNo ")
//    @PutMapping("/addTryCount")
//    public ResponseEntity<?> addTryShorts(@RequestHeader("Authorization") String accessToken, @RequestBody Map<String, String> data) {
//        try {
//            int shortsNo = Integer.parseInt(data.get("shortsNo"));
//
//            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
//            System.out.println("username : "+ username);
//
//            shortsService.addTryCount(username,shortsNo);
//            return new ResponseEntity<>("Successful Try Counting", HttpStatus.OK);
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }

//    @Operation(summary = "DB에 유튜브 url 올리기", description = "헤더에 accessToken 넣기, RequestParam으로 uploadNo,youtubeUrl ")
//    @PutMapping("/youtubeUrl/{uploadNo}/{videoId}")
//    public ResponseEntity<?> putYoutubeUrl(@PathVariable int uploadNo, @PathVariable String videoId) {
//        try {
//            System.out.println(uploadNo + " " +videoId);
//            String url = "https://www.youtube.com/watch?v=" + videoId;
//            shortsService.putYoutubeUrl(uploadNo,url);
//
//            return new ResponseEntity<>("Sucessful save DB!", HttpStatus.OK);
//
//        } catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }


}
