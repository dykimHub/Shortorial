package com.sleep.sleep.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Exception {
    INVALIDATE_USER(1000,"해당 회원이 존재하지 않습니다."),
    INVALIDATE_SHORTS(1001,"해당 쇼츠가 존재하지 않습니다.");

    private final int code;
    private final String message;

}
