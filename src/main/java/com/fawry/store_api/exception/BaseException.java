package com.fawry.store_api.exception;

import com.fawry.store_api.enums.ErrorCode;

public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public BaseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}