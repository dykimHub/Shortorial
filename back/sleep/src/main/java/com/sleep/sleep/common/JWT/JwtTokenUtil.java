package com.sleep.sleep.common.JWT;

import com.sleep.sleep.member.entity.MemberRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static com.sleep.sleep.common.JWT.JwtExpiration.ACCESS_TOKEN_EXPIRATION_TIME;
import static com.sleep.sleep.common.JWT.JwtExpiration.REFRESH_TOKEN_EXPIRATION_TIME;

@Slf4j
@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public Claims extractAllClaims (String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(SECRET_KEY))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsername(String token) {
        return extractAllClaims(token).get("username", String.class);
    }

    public int getUserId(String token) {
        return extractAllClaims(token).get("userId", Integer.class);
    }

    public String getUserRole(String token) {
        return extractAllClaims(token).get("userRole", String.class);
    }

    private Key getSigningKey(String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenExpired(String token) {
        try {
            final Claims claims = extractAllClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true; // 만료된 토큰
        }
    }

    public String generateAccessToken(String username, int userId, String role) {
        return doGenerateToken(username, userId, role, ACCESS_TOKEN_EXPIRATION_TIME.getValue());
    }

    public String generateRefreshToken(String username, int userId, String role) {
        return doGenerateToken(username, userId, role, REFRESH_TOKEN_EXPIRATION_TIME.getValue());
    }

    private String doGenerateToken(String username, int userId, String role, long expireTime) { // 1
        Claims claims = Jwts.claims();
        claims.put("username", username);
        claims.put("userId", userId);
        claims.put("userRole", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(getSigningKey(SECRET_KEY), SignatureAlgorithm.HS256)
                .compact();
    }


    public long getRemainMilliSeconds(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        Date now = new Date();
        return expiration.getTime() - now.getTime();
    }
}
