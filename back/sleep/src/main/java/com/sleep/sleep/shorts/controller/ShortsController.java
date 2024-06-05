package com.sleep.sleep.shorts.controller;

import com.sleep.sleep.common.JWT.JwtTokenUtil;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.repository.UploadShortsRepository;
import com.sleep.sleep.shorts.service.ShortsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/shorts")
@RestController
public class ShortsController {

    private final ShortsService shortsService;
    private final JwtTokenUtil jwtTokenUtil;
    private final UploadShortsRepository uploadShortsRepository;

    @Operation(summary = "쇼츠 목록 조회")
    @GetMapping
    public ResponseEntity<List<ShortsDto>> selectShortList() {

        List<ShortsDto> shortsList = shortsService.getShortList();

        return ResponseEntity.ok(shortsList);
    }

    @Operation(summary = "특정 쇼츠 조회")
    @GetMapping("/{shortsNo}")
    public ResponseEntity<ShortsDto> selectUserPrediction(@PathVariable int shortsNo) {
        return ResponseEntity.ok(shortsService.getShortsInfo(shortsNo));
    }

    @Operation(summary = "동영상 파일 이름 중복검사", description = "헤더에 accessToken 넣기, RequestParam으로 title 받기. true면 이미 있는 이름; false면 사용 가능한 이름 ")
    @GetMapping("/checkName")
    public ResponseEntity<?> checkName(@RequestHeader("Authorization") String accessToken, @RequestBody Map<String, String> data) {
        try {
            String title = data.get("title");

            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
            System.out.println("username : "+ username);

            Boolean possible = shortsService.isNameExists(username+"/"+title);

            if (possible) {
                log.info("사용 가능 여부 : "+ possible);
                return new ResponseEntity<>("Name is available.", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Name already exists. Please choose another one.", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "시도한 영상 카운트 올리기", description = "헤더에 accessToken 넣기, RequestParam으로 shortsNo ")
    @PutMapping("/addTryCount")
    public ResponseEntity<?> addTryShorts(@RequestHeader("Authorization") String accessToken, @RequestBody Map<String, String> data) {
        try {
            int shortsNo = Integer.parseInt(data.get("shortsNo"));

            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
            System.out.println("username : "+ username);

            shortsService.addTryCount(username,shortsNo);
            return new ResponseEntity<>("Successful Try Counting", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "DB에 유튜브 url 올리기", description = "헤더에 accessToken 넣기, RequestParam으로 uploadNo,youtubeUrl ")
    @PutMapping("/youtubeUrl/{uploadNo}/{videoId}")
    public ResponseEntity<?> putYoutubeUrl(@PathVariable int uploadNo, @PathVariable String videoId) {
        try {
            System.out.println(uploadNo + " " +videoId);
            String url = "https://www.youtube.com/watch?v=" + videoId;
            shortsService.putYoutubeUrl(uploadNo,url);

            return new ResponseEntity<>("Sucessful save DB!", HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "인기순 쇼츠 조회", description = "헤더에 accessToken 넣기")
    @GetMapping("/topRanking")
    public ResponseEntity<?> shortsGetOrderByChallengers() {

        List<ShortsDto> shortRankingList = shortsService.getShortRankingList();

        return ResponseEntity.ok(shortRankingList);
    }




    private String resolveToken(String accessToken) {
        log.info("resolveToken, AccessToken: "+ accessToken.toString());
        return accessToken.substring(7);
    }



}
