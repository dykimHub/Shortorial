package com.sleep.sleep.member.service;

import com.sleep.sleep.common.JWT.TokenInfo;
import com.sleep.sleep.exception.SuccessResponse;
import com.sleep.sleep.member.dto.JoinDto;
import com.sleep.sleep.member.dto.MemberInfoDto;
import com.sleep.sleep.member.dto.OriginLoginRequestDto;
import com.sleep.sleep.member.entity.Member;

public interface MemberService {

    boolean checkDup(String category, String input);

    SuccessResponse join(JoinDto dto);

    TokenInfo login(OriginLoginRequestDto dto);

    MemberInfoDto getMemberInfo(String accessToken);

    SuccessResponse logout(TokenInfo tokenInfo);

    TokenInfo reissue(String refreshToken);

    Member findByMemberId(String memberId);

    int getMemberIndex(String accessToken);

    String getMemberId(String accessToken);
}
