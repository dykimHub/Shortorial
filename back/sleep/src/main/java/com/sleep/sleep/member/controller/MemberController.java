package com.sleep.sleep.member.controller;


import com.sleep.sleep.common.JWT.JwtTokenUtil;
import com.sleep.sleep.common.JWT.TokenInfo;
import com.sleep.sleep.member.dto.JoinDto;
import com.sleep.sleep.member.dto.MemberInfoDto;
import com.sleep.sleep.member.dto.OriginLoginRequestDto;
import com.sleep.sleep.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;

    @Operation(summary = "카테고리 별 중복 검사.아이디, 닉네임, 이메일")
    @GetMapping("/check/{category}/{input}")
    public ResponseEntity<Map<String, Object>> dupCheck(@PathVariable String category,@PathVariable String input){
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        try{
            boolean dupCheck = memberService.checkDup(category,input);
            resultMap.put("dupCheck", dupCheck);
        }catch(Exception e){
            log.info(e.getMessage());
            resultMap.put("message", e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status).body(resultMap);
    }



    @Operation(summary = "일반 로그인")
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> originLogin(@RequestBody OriginLoginRequestDto originLoginRequestDto) {
        log.info("일반 로그인");
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        TokenInfo accessToken = null;

        try {
            accessToken = memberService.login(originLoginRequestDto);
            resultMap.put("accessToken", accessToken);

        } catch (Exception e) {
            log.info(e.getMessage());
            resultMap.put("message", e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status)
                .body(resultMap);
    }

    @Operation(summary = "회원가입")
    @PostMapping("/join")
    public ResponseEntity<Map<String, Object>> join(@RequestBody JoinDto joinDto) {
        log.info("회원 가입");
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        try {
            memberService.join(joinDto);
        } catch (Exception e) {
            log.info(e.getMessage());
            resultMap.put("message", e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status)
                .body(resultMap);
    }

    @Operation(summary = "회원정보조회")
    @GetMapping("/info")
    public ResponseEntity<MemberInfoDto> memberFind(@RequestHeader("Authorization") String accessToken) {
        //log.info("멤버 정보 조회");
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        MemberInfoDto memberInfoDto = null;
        try {
            //System.out.println(accessToken.toString());
            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
            //System.out.println("username : "+ username);
            memberInfoDto = memberService.getMemberInfo(username);
//            System.out.println("memberInfo : "+ memberInfoDto.getMemberNickname());
//            resultMap.put("memberInfo", memberInfoDto);
        } catch (Exception e) {
            log.info(e.getMessage());
            resultMap.put("message", e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status)
                .body(memberInfoDto);
    }

    @Operation(summary = "리프레시토큰 재발급")
    @GetMapping("/reissue")
    public ResponseEntity<Map<String, Object>> reissue(@RequestHeader("RefreshToken") String refreshToken) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        TokenInfo accessToken = null;
        try {
            accessToken = memberService.reissue(refreshToken);
            resultMap.put("accessToken", accessToken);
        } catch (Exception e) {
            log.info(e.getMessage());
            resultMap.put("message", e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status)
                .body(resultMap);
    }

    @Operation(summary = "로그아웃")
    @GetMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String accessToken,
                                                      @RequestHeader("RefreshToken") String refreshToken) {
        Map<String, Object> resultMap = new HashMap<>();
        HttpStatus status = HttpStatus.OK;
        try {
            String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
            memberService.logout(TokenInfo.of(accessToken, refreshToken), username);
            resultMap.put("message", "로그아웃 되었습니다.");
        } catch (Exception e) {
            log.info(e.getMessage());
            resultMap.put("message", e.getMessage());
            status = HttpStatus.BAD_REQUEST;
        }
        return ResponseEntity.status(status)
                .body(resultMap);
    }


    private String resolveToken(String accessToken) {
        //log.info("resolveToken, AccessToken: "+ accessToken.toString());
        return accessToken.substring(7);
    }
}
