package com.sleep.sleep.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_001", "회원을 찾을 수 없습니다."),

    SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND, "SHORTS_001", "쇼츠를 찾을 수 없습니다."),
    ALL_SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND, "SHORTS_002", "쇼츠 리스트가 비어있습니다."),
    POPULAR_SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND, "SHORTS_003", "인기 쇼츠 리스트가 비어있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
