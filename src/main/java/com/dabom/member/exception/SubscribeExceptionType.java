package com.dabom.member.exception;

import com.dabom.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public enum SubscribeExceptionType implements ExceptionType {

    DUPLICATED_SUBSCRIBE(HttpStatus.NOT_FOUND, "이미 구독했습니다."),
    NOTFOUND_SUBSCRIBE(HttpStatus.NOT_FOUND, "구독하지 않았습니다.");;

    private final HttpStatus httpStatusCode;
    private final String message;

    SubscribeExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatusCode = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus statusCode() {
        return this.httpStatusCode;
    }

    @Override
    public String message() {
        return this.message;
    }
}
