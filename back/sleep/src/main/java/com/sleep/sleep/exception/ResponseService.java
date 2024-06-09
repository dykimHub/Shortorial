package com.sleep.sleep.exception;

import org.springframework.stereotype.Service;

@Service
public class ResponseService {

    public CommonResponse getErrorResponse(int code, String message) {
        CommonResponse errorResponse = CommonResponse.builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
        return errorResponse;
    }


}
