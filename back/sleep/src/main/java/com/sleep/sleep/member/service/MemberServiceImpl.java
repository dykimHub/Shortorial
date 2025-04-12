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
import com.sleep.sleep.exception.CustomException;
import com.sleep.sleep.exception.ExceptionCode;
import com.sleep.sleep.exception.SuccessResponse;
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
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * 각 카테고리에 맞춰서 중복 여부를 반환합니다.
     *
     * @param category 아이디 혹은 닉네임
     * @param input    사용자가 입력한 아이디 혹은 닉네임 값
     * @return true: 이미 있는 값, false: 새로운 값
     */
    @Override
    public boolean checkDup(String category, String input) {
        return switch (category) {
            case "id" -> memberRepository.existsByMemberId(input);
            case "nickname" -> memberRepository.existsByMemberNickname(input);
            default -> throw new CustomException(ExceptionCode.INVALID_CATEGORY);
        };
    }

    @Override
    public SuccessResponse join(JoinDto dto) {
        Member newMember = memberRepository.save(Member.builder()
                .memberId(dto.getMemberId())
                .memberPass(passwordEncoder.encode(dto.getMemberPass()))
                .memberNickname(dto.getMemberNickname())
//                .memberProfile(dto.getMemberProfile())
                .memberRole(MemberRole.UESR)
                .build());
        log.info("회원 {}번 가입", newMember.getMemberIndex());
        return SuccessResponse.of("회원 가입에 성공했습니다.");
    }

    @Override
    public TokenInfo login(OriginLoginRequestDto dto) {
        Member member = findByMemberId(dto.getMemberId());
        checkPassword(dto.getMemberPass(), member.getMemberPass());

        String username = member.getMemberId();
        int userId = member.getMemberIndex();
        String role = member.getMemberRole().name();
        log.info("회원 {}번 로그인", userId);

        String accessToken = jwtTokenUtil.generateAccessToken(username, userId, role);
        log.info("회원 {}번 엑세스 토큰 등록", userId);
        RefreshToken refreshToken = saveRefreshToken(username, userId, role);
        log.info("회원 {}번 리프레시 토큰 등록", userId);
        return TokenInfo.of(accessToken, refreshToken.getRefreshToken());
    }

    private void checkPassword(String rawPassword, String findMemberPassword) {
        if (!passwordEncoder.matches(rawPassword, findMemberPassword)) {
            throw new CustomException(ExceptionCode.INVALID_PASSWORD);
        }
    }

    private RefreshToken saveRefreshToken(String username, int userId, String role) {
        return refreshTokenRedisRepository.save(RefreshToken.createRefreshToken(
                        username,
                        jwtTokenUtil.generateRefreshToken(username, userId, role),
                        REFRESH_TOKEN_EXPIRATION_TIME.getValue()
                )
        );
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
    public SuccessResponse logout(TokenInfo tokenInfo) {
        String accessToken = resolveToken(tokenInfo.getAccessToken());
        String username = getMemberId(accessToken);

        long remainMilliSeconds = jwtTokenUtil.getRemainMilliSeconds(accessToken);
        refreshTokenRedisRepository.deleteById(username);
        logoutAccessTokenRedisRepository.save(LogoutAccessToken.of(accessToken, username, remainMilliSeconds));

        log.info("회원 {}이 로그아웃 했습니다.", username);
        return SuccessResponse.of("로그아웃에 성공했습니다.");
    }

    private String resolveToken(String token) {
        return token.substring(7);
    }

    @Override
    public TokenInfo reissue(String refreshToken) {
        refreshToken = resolveToken(refreshToken);
        String username = getCurrentUsername();
        RefreshToken redisRefreshToken = refreshTokenRedisRepository.findById(username).orElseThrow(NoSuchElementException::new);

        int userId = getCurrentUserId();
        String role = getCurrentUserRole();

        if (refreshToken.equals(redisRefreshToken.getRefreshToken())) {
            return reissueRefreshToken(refreshToken, username, userId, role);
        }
        throw new CustomException(ExceptionCode.INVALID_TOKEN);
    }

    private TokenInfo reissueRefreshToken(String refreshToken, String username, int userId, String role) {
        if (lessThanReissueExpirationTimesLeft(refreshToken)) {
            log.info("회원 {}번 리프레시 토큰 재발급", userId);
            return TokenInfo.of(jwtTokenUtil.generateAccessToken(username, userId, role), saveRefreshToken(username, userId, role).getRefreshToken());
        }
        log.info("회원 {}번 엑세스 토큰 재발급", userId);
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
        return memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEMBER_NOT_FOUND));
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
