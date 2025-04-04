package com.sleep.sleep.member.service;

import com.sleep.sleep.common.JWT.JwtExpiration;
import com.sleep.sleep.common.JWT.JwtTokenUtil;
import com.sleep.sleep.common.JWT.TokenInfo;
import com.sleep.sleep.common.JWT.UserDetailsImpl;
import com.sleep.sleep.common.redis.entity.CacheKey;
import com.sleep.sleep.common.redis.entity.LogoutAccessToken;
import com.sleep.sleep.common.redis.entity.RefreshToken;
import com.sleep.sleep.common.redis.repository.LogoutAccessTokenRedisRepository;
import com.sleep.sleep.common.redis.repository.RefreshTokenRedisRepository;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

import static com.sleep.sleep.common.JWT.JwtExpiration.REFRESH_TOKEN_EXPIRATION_TIME;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean checkDup(String category, String input) {
        //category로 id, nickname의 값이 넘어오면 해당하는 중복검사 실행
        //true = 해당하는 값이 이미 있음
        boolean check = switch (category) {
            case "id" -> memberRepository.existsByMemberId(input);
            case "nickname" -> memberRepository.existsByMemberNickname(input);
            default -> throw new IllegalStateException("올바른 category가 아닙니다. ");
        };
        return check;
    }

    @Override
    public void join(JoinDto dto) {
        memberRepository.save(Member.builder()
                .memberId(dto.getMemberId())
                .memberPass(passwordEncoder.encode(dto.getMemberPass()))
                .memberNickname(dto.getMemberNickname())
//                .memberProfile(dto.getMemberProfile())
                .memberRole(MemberRole.UESR)
                .build());
    }

    @Override
    public TokenInfo login(OriginLoginRequestDto dto) {
        Member member = findByMemberId(dto.getMemberId());
        checkPassword(dto.getMemberPass(), member.getMemberPass());

        String username = member.getMemberId();
        int userId = member.getMemberIndex();
        String role = member.getMemberRole().name();
        String accessToken = jwtTokenUtil.generateAccessToken(username, userId, role);
        RefreshToken refreshToken = saveRefreshToken(username, userId, role);
        log.info("로그인 성공");
        return TokenInfo.of(accessToken, refreshToken.getRefreshToken());
    }

    private void checkPassword(String rawPassword, String findMemberPassword) {
        if (!passwordEncoder.matches(rawPassword, findMemberPassword)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
    }

    private RefreshToken saveRefreshToken(String username, int userId, String role) {
        log.info("RefreshToken 등록");
        return refreshTokenRedisRepository.save(RefreshToken.createRefreshToken(username,
                jwtTokenUtil.generateRefreshToken(username, userId, role), REFRESH_TOKEN_EXPIRATION_TIME.getValue()));
    }

    @Override
    public MemberInfoDto getMemberInfo(String accessToken) {
        String username = jwtTokenUtil.getUsername(resolveToken(accessToken));
        Member member = findByMemberId(username);

        return MemberInfoDto.builder()
                .memberIndex(member.getMemberIndex())
                .memberId(member.getMemberId())
                .memberNickname(member.getMemberNickname())
                .memberProfile(member.getMemberProfile())
                .memberRole(member.getMemberRole())
                .build();
    }

    @Override
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

    @Override
    public TokenInfo reissue(String refreshToken) {
        refreshToken = resolveToken(refreshToken);
        String username = getCurrentUsername();
        int userId = getCurrentUserId();
        String role = getCurrentUserRole();
        RefreshToken redisRefreshToken = refreshTokenRedisRepository.findById(username).orElseThrow(NoSuchElementException::new);

        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {
            return reissueRefreshToken(refreshToken, username, userId, role);
        }
        throw new IllegalArgumentException("토큰이 일치하지 않습니다.");
    }

    private TokenInfo reissueRefreshToken(String refreshToken, String username, int userId, String role) {
        if (lessThanReissueExpirationTimesLeft(refreshToken)) {
            //System.out.println("리프레시 토큰도 재발급");
            return TokenInfo.of(jwtTokenUtil.generateAccessToken(username, userId, role), saveRefreshToken(username, userId, role).getRefreshToken());
        }
        //System.out.println("엑세스 토큰만 재발급");
        return TokenInfo.of(jwtTokenUtil.generateAccessToken(username, userId, role), refreshToken);
    }

    private boolean lessThanReissueExpirationTimesLeft(String refreshToken) {
        return jwtTokenUtil.getRemainMilliSeconds(refreshToken) < JwtExpiration.REISSUE_EXPIRATION_TIME.getValue();
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        return principal.getUsername();
    }

    private int getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return principal.getUserId();
    }

    private String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
        return principal.getRole();
    }


    @Override
    public Member findByMemberId(String memberId) {
        return memberRepository.findByMemberId(memberId).orElseThrow(
                () -> new NoSuchElementException("회원이 없습니다."));
    }

    @Override
    public int getMemberIndex(String accessToken) {
        return jwtTokenUtil.getUserId(resolveToken(accessToken));
    }

    @Override
    public String getMemberId(String accessToken) {
        return jwtTokenUtil.getUsername(resolveToken(accessToken));
    }


}
