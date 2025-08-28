package com.dabom.together.exception;

import com.dabom.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public enum TogetherExceptionMessages implements ExceptionType {
    NOT_VALID_CODE(HttpStatus.NOT_FOUND, "해당하는 Together 방이 존재하지 않습니다. 코드를 확인해주세요."),
    MAX_TOGETHER_MEMBER(HttpStatus.BAD_REQUEST, "해당하는 Together 방의 정원이 다 찼습니다. 다른 방을 찾아주세요."),
    NOT_MASTER_MEMBER(HttpStatus.FORBIDDEN, "방의 만든 멤버가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String message;

    TogetherExceptionMessages(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus statusCode() {
        return httpStatus;
    }

    @Override
    public String message() {
        return message;
    }
}
