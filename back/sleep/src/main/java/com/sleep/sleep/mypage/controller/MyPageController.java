package com.sleep.sleep.mypage.controller;

import com.sleep.sleep.common.JWT.JwtTokenUtil;
import com.sleep.sleep.mypage.service.MyPageService;
import com.sleep.sleep.shorts.dto.ShortsDto;
import com.sleep.sleep.shorts.dto.TriedShortsDto;
import com.sleep.sleep.shorts.dto.RecordedShortsDto;
import com.sleep.sleep.shorts.service.ShortsService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/mypage")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MyPageController {

//    private final ShortsService shortsService;
//    private final JwtTokenUtil jwtTokenUtil;
//    private final MyPageService myPageService;

//    @Operation(summary = "사용자의 업로드한 영상 리스트", description ="사용자의 엑세스 토큰 필요함")
//    @GetMapping("/upload-shorts")
//    public ResponseEntity<List<RecordedShortsDto>> selectUploadShortList(@RequestHeader("Authorization") String accessToken) {
//        //사용자 찾기
//        String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
//        System.out.println("username : "+ username);
//
//        List<RecordedShortsDto> shortsList = shortsService.getUploadShortsList(username);
//
//        return ResponseEntity.ok(shortsList);
//    }
//
//    @Operation(summary = "사용자가 시도한 영상 리스트", description ="사용자의 엑세스 토큰 필요함")
//    @GetMapping("/try-shorts")
//    public ResponseEntity<List<ShortsDto>> selectTryShortList(@RequestHeader("Authorization") String accessToken) {
//        //사용자 찾기
//        String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
//        System.out.println("username : "+ username);
//
//        List<ShortsDto> shortsList = shortsService.getTryShortsList(username);
//
//        return ResponseEntity.ok(shortsList);
//    }
//
//    @Operation(summary = "프로플-카운터", description ="header에 accessToken")
//    @GetMapping("/counting")
//    public ResponseEntity<Map<String,Integer>> getCounting(@RequestHeader("Authorization") String accessToken) {
//        //사용자 찾기
//        String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
//        System.out.println("username : "+ username);
//
//        Map<String,Integer> result = myPageService.getCount(username);
//
//        return ResponseEntity.ok(result);
//    }
//
//
//    private String resolveToken(String accessToken) {
//        log.info("resolveToken, AccessToken: "+ accessToken.toString());
//        return accessToken.substring(7);
//    }

}
