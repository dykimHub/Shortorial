package com.sleep.sleep.exception;

public class InvalidateUserException extends RuntimeException {

    private final Exception exception;

    public InvalidateUserException() {
        super(Exception.INVALIDATE_USER.getMessage());
        this.exception = Exception.INVALIDATE_USER;
    }

}
