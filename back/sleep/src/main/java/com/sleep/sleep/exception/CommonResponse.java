package com.sleep.sleep.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CommonResponse {
    boolean success;
    int code;
    String message;

}
