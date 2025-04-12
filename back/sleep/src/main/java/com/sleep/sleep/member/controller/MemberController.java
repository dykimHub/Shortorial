package com.sleep.sleep.member.controller;


import com.sleep.sleep.common.JWT.TokenInfo;
import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.member.dto.JoinDto;
import com.sleep.sleep.member.dto.MemberInfoDto;
import com.sleep.sleep.member.dto.OriginLoginRequestDto;
import com.sleep.sleep.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "카테고리 별 중복 검사: 아이디, 닉네임, 이메일")
    @GetMapping("/check/{category}/{input}")
    public ResponseEntity<Boolean> dupCheck(@PathVariable String category, @PathVariable String input) {
        boolean isDup = memberService.checkDup(category, input);
        return ResponseEntity.ok()
                .body(isDup);
    }

    @Operation(summary = "일반 로그인")
    @PostMapping("/login")
    public ResponseEntity<TokenInfo> originLogin(@RequestBody OriginLoginRequestDto originLoginRequestDto) {
        TokenInfo tokenInfo = memberService.login(originLoginRequestDto);
        return ResponseEntity.ok()
                .body(tokenInfo);
    }

    @Operation(summary = "회원가입")
    @PostMapping("/join")
    public ResponseEntity<SuccessResponse> join(@RequestBody JoinDto joinDto) {
        SuccessResponse successResponse = memberService.join(joinDto);
        return ResponseEntity.ok()
                .body(successResponse);
    }

    @Operation(summary = "회원정보 조회")
    @GetMapping("/info")
    public ResponseEntity<MemberInfoDto> memberFind(@RequestHeader("Authorization") String accessToken) {
        MemberInfoDto memberInfoDto = memberService.getMemberInfo(accessToken);
        return ResponseEntity.ok()
                .body(memberInfoDto);
    }

    @Operation(summary = "RefreshToken 재발급")
    @GetMapping("/reissue")
    public ResponseEntity<TokenInfo> reissue(@RequestHeader("RefreshToken") String refreshToken) {
        TokenInfo tokenInfo = memberService.reissue(refreshToken);
        return ResponseEntity.ok()
                .body(tokenInfo);
    }

    @Operation(summary = "로그아웃")
    @GetMapping("/logout")
    public ResponseEntity<SuccessResponse> logout(@RequestHeader("Authorization") String accessToken,
                                                  @RequestHeader("RefreshToken") String refreshToken) {
        SuccessResponse successResponse = memberService.logout(TokenInfo.of(accessToken, refreshToken));
        return ResponseEntity.ok()
                .body(successResponse);
    }
}
