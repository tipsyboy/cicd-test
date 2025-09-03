package com.dabom.member.exception;

import com.dabom.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public enum MemberExceptionType implements ExceptionType {

    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 유저를 찾을 수 없습니다. 회원가입을 진행해주세요"),
    DUPLICATED_CHANNEL_NAME(HttpStatus.NOT_FOUND, "같은 채널의 이름이 존재합니다. 다른 이름을 입력해주세요"),
    DUPLICATED_SIGNUP(HttpStatus.NOT_FOUND, "이미 회원가입이 되어있습니다. 로그인을 진행해주세요");;

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
