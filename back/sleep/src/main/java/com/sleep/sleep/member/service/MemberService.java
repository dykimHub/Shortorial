package com.sleep.sleep.member.service;

import com.sleep.sleep.common.JWT.JwtExpiration;
import com.sleep.sleep.common.JWT.JwtTokenUtil;
import com.sleep.sleep.common.JWT.TokenInfo;
import com.sleep.sleep.common.redis.entity.CacheKey;
import com.sleep.sleep.common.redis.entity.LogoutAccessToken;
import com.sleep.sleep.common.redis.entity.RefreshToken;
import com.sleep.sleep.common.redis.repository.LogoutAccessTokenRedisRepository;
import com.sleep.sleep.common.redis.repository.RefreshTokenRedisRepository;
import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.member.dto.JoinDto;
import com.sleep.sleep.member.dto.MemberInfoDto;
import com.sleep.sleep.member.dto.OriginLoginRequestDto;
import com.sleep.sleep.member.entity.Member;
import com.sleep.sleep.member.entity.MemberRole;
import com.sleep.sleep.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static com.sleep.sleep.common.JWT.JwtExpiration.REFRESH_TOKEN_EXPIRATION_TIME;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    public boolean checkDup(String category, String input) {
        //category로 id, nickname의 값이 넘어오면 해당하는 중복검사 실행
        //true = 해당하는 값이 이미 있음
        boolean check = switch (category){
            case "id"->memberRepository.existsByMemberId(input);

            case "nickname" -> memberRepository.existsByMemberNickname(input);

            default -> throw new IllegalStateException("올바른 category가 아닙니다. ");
        };
        return check;
    }

    public void join(JoinDto dto) {
        memberRepository.save(Member.builder()
                .memberId(dto.getMemberId())
                .memberPass(passwordEncoder.encode(dto.getMemberPass()))
                .memberNickname(dto.getMemberNickname())
//                .memberProfile(dto.getMemberProfile())
                .memberRole(MemberRole.UESR)
                .build());
    }
    public TokenInfo login(OriginLoginRequestDto dto) {
        Member member = memberRepository.findByMemberId(dto.getMemberId()).orElseThrow(
                () -> new NoSuchElementException("회원이 없습니다."));
        checkPassword(dto.getMemberPass(), member.getPassword());

        String username = member.getUsername();
        String accessToken = jwtTokenUtil.generateAccessToken(username);
        RefreshToken refreshToken = saveRefreshToken(username);
        log.info("로그인 성공");
        return TokenInfo.of(accessToken, refreshToken.getRefreshToken());
    }

    private void checkPassword(String rawPassword, String findMemberPassword) {
        if (!passwordEncoder.matches(rawPassword, findMemberPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }
    private RefreshToken saveRefreshToken(String username) {
        log.info("RefreshToken 등록");
        return refreshTokenRedisRepository.save(RefreshToken.createRefreshToken(username,
                jwtTokenUtil.generateRefreshToken(username), REFRESH_TOKEN_EXPIRATION_TIME.getValue()));
    }

    public MemberInfoDto getMemberInfo(String username) {
        Member member = memberRepository.findByMemberId(username).orElseThrow(() -> new NoSuchElementException("회원이 없습니다."));

        return MemberInfoDto.builder()
                .username(member.getUsername())
                .memberNickname(member.getMemberNickname())
                .memberProfile(member.getMemberProfile())
                .build();
    }


    @CacheEvict(value = CacheKey.USER, key = "#username")
    public void logout(TokenInfo tokenDto, String username) {
        String accessToken = resolveToken(tokenDto.getAccessToken());
        long remainMilliSeconds = jwtTokenUtil.getRemainMilliSeconds(accessToken);
        refreshTokenRedisRepository.deleteById(username);
        logoutAccessTokenRedisRepository.save(LogoutAccessToken.of(accessToken, username, remainMilliSeconds));
    }

    private String resolveToken(String token) {
        return token.substring(7);
    }

    public TokenInfo reissue(String refreshToken) {
        refreshToken = resolveToken(refreshToken);
        String username = getCurrentUsername();
        RefreshToken redisRefreshToken = refreshTokenRedisRepository.findById(username).orElseThrow(NoSuchElementException::new);

        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {
            return reissueRefreshToken(refreshToken, username);
        }
        throw new IllegalArgumentException("토큰이 일치하지 않습니다.");
    }
    private TokenInfo reissueRefreshToken(String refreshToken, String username) {
        if (lessThanReissueExpirationTimesLeft(refreshToken)) {
            System.out.println("리프레시 토큰도 재발급");
            return TokenInfo.of(jwtTokenUtil.generateAccessToken(username), saveRefreshToken(username).getRefreshToken());
        }
        System.out.println("엑세스 토큰만 재발급");
        return TokenInfo.of(jwtTokenUtil.generateAccessToken(username), refreshToken);
    }

    private boolean lessThanReissueExpirationTimesLeft(String refreshToken) {
        return jwtTokenUtil.getRemainMilliSeconds(refreshToken) < JwtExpiration.REISSUE_EXPIRATION_TIME.getValue();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return principal.getUsername();
    }

    /**
     * token에서 memberId를 찾아서 Member 객체를 반환함
     *
     * @param accessToken 로그인한 회원의 token
     * @return 로그인한 Member 객체
     * @throws CustomException 해당 Member 객체를 찾을 수 없음
     */
    public Member findMemberEntity(String accessToken) {
        String memberId = jwtTokenUtil.getUsername(accessToken.substring(7));
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));

        return member;
    }
}
