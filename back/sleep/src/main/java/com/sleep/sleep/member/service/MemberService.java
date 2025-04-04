package com.sleep.sleep.member.service;

import com.sleep.sleep.common.JWT.TokenInfo;
import com.sleep.sleep.common.redis.entity.CacheKey;
import com.sleep.sleep.member.dto.JoinDto;
import com.sleep.sleep.member.dto.MemberInfoDto;
import com.sleep.sleep.member.dto.OriginLoginRequestDto;
import com.sleep.sleep.member.entity.Member;
import org.springframework.cache.annotation.CacheEvict;

public interface MemberService {

    boolean checkDup(String category, String input);

    void join(JoinDto dto);

    TokenInfo login(OriginLoginRequestDto dto);

    MemberInfoDto getMemberInfo(String accessToken);

    void logout(TokenInfo tokenDto, String username);

    TokenInfo reissue(String refreshToken);

    Member findByMemberId(String memberId);

    int getMemberIndex(String accessToken);

    String getMemberId(String accessToken);
}
