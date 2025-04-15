package com.sleep.sleep.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_001", "회원을 찾을 수 없습니다."),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "MEMBER_002", "권한이 없습니다. 다시 로그인 해주세요."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "MEMBER_003", "존재하지 않는 카테고리 입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER_004", "일치하지 않는 비밀번호 입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "MEMBER_005", "유효하지 않은 토큰입니다."),

    SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND, "SHORTS_001", "쇼츠를 찾을 수 없습니다."),
    ALL_SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND, "SHORTS_002", "쇼츠 리스트가 비어있습니다."),
    POPULAR_SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND, "SHORTS_003", "인기 쇼츠 리스트가 비어있습니다."),
    SHORTS_STATS_NOT_FOUND(HttpStatus.NOT_FOUND, "SHORTS_005", "회원의 쇼츠 통계를 계산할 수 없습니다."),
    TRIED_SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND, "SHORTS_004", "회원이 시도한 쇼츠를 찾을 수 없습니다."),
    RECORDED_SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND, "SHORTS_005", "회원이 녹화한 쇼츠를 찾을 수 없습니다."),
    EXISTED_RECORDED_SHORTS_TITLE(HttpStatus.BAD_REQUEST, "SHORTS_006", "이미 존재하는 제목입니다."),
    RECORDED_SHORTS_UPDATE_FAILED(HttpStatus.BAD_REQUEST, "SHORTS_007", "회원이 녹화한 쇼츠의 상태를 변경할 수 없습니다."),

    RECORDED_FILE_NULL(HttpStatus.BAD_REQUEST, "S3_001", "회원이 녹화한 파일이 없습니다."),
    S3OBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "S3_002", "해당 S3 객체(Key)는 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
