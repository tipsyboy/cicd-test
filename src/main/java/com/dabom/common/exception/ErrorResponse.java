package com.dabom.common.exception;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final Integer code;
    private final String message;

    private ErrorResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ErrorResponse from(BaseException e) {
        ExceptionType exceptionType = e.getExceptionType();
        return new ErrorResponse(exceptionType.statusCode().value(), exceptionType.message());
    }

    public static ErrorResponse of(int status, String message) {
        return new ErrorResponse(status, message);
    }
}
