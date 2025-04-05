package com.sleep.sleep.common.JWT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleep.sleep.common.redis.repository.LogoutAccessTokenRedisRepository;
import com.sleep.sleep.exception.ExceptionCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final LogoutAccessTokenRedisRepository logoutAccessTokenRedisRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = resolveToken(request);
        if (accessToken != null) {
            checkLogout(accessToken);
            if (!validateAccessToken(accessToken, response)) {
                return; // 토큰 만료 응답 보냈으므로 더 이상 진행 X
            }

            // 회원 DB 조회하지 않고 토큰에서 회원정보 파싱
            String username = jwtTokenUtil.getUsername(accessToken);
            int userId = jwtTokenUtil.getUserId(accessToken);
            String userRole = jwtTokenUtil.getUserRole(accessToken);

            UserDetails userDetails = UserDetailsImpl.of(username, userId, userRole);
            processSecurity(request, userDetails);
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void checkLogout(String accessToken) {
        if (logoutAccessTokenRedisRepository.existsById(accessToken)) {
            throw new IllegalArgumentException("로그아웃된 회원입니다.");
        }
    }

    private boolean validateAccessToken(String accessToken, HttpServletResponse response) throws IOException {
        if (jwtTokenUtil.isTokenExpired(accessToken)) { // 만료된 토큰이면
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8"); // json 반환

            ExceptionCode exceptionCode = ExceptionCode.ACCESS_TOKEN_EXPIRED; // 토큰 만료용 예외

            Map<String, String> errorResponse = Map.of(
                    "status", exceptionCode.getCode(),
                    "code", exceptionCode.getCode(),
                    "message", exceptionCode.getMessage()
            );
            String json = new ObjectMapper().writeValueAsString(errorResponse); // map을 json으로 변환

            response.getWriter().write(json); // response에 json 포함
            return false;
        }

        return true; // 유효
    }

    private void processSecurity(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }


}
