package com.dabom.member.exception;

import com.dabom.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public enum MemberExceptionType implements ExceptionType {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 를 찾을 수 없습니다.");

    private final HttpStatus httpStatusCode;
    private final String message;

    MemberExceptionType(HttpStatus httpStatus, String message) {
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
