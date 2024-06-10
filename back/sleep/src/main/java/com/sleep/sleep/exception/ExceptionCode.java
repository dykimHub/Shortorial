package com.sleep.sleep.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND, "SHORTS_001", "해당되는 id의 쇼츠가 존재하지 않습니다."),
    ALL_SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND,"SHORTS_002","쇼츠 리스트가 비어 있습니다."),
    POPULAR_SHORTS_NOT_FOUND(HttpStatus.NOT_FOUND,"SHORTS_003","인기 쇼츠 리스트가 비어 있습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

}
