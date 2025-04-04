package com.sleep.sleep.common.JWT;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JwtExpiration {
    ACCESS_TOKEN_EXPIRATION_TIME("Access Token 만료 시간 / 30분", 1000L * 60 * 30),                // 30분
    REFRESH_TOKEN_EXPIRATION_TIME("Refresh Token 만료 시간 / 14일", 1000L * 60 * 60 * 24 * 14),     // 14일
    REISSUE_EXPIRATION_TIME("Refresh Token 재발급 기준 시간 / 7일", 1000L * 60 * 60 * 24 * 7);       // 7일

    private String description;
    private Long value;
}
