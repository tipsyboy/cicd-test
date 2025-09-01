package com.dabom.channelboard.exception;

import com.dabom.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public enum ChannelBoardExceptionMessages2 implements ExceptionType {
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 게시글을 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 멤버를 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "해당 게시글에 대한 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ChannelBoardExceptionMessages2(HttpStatus httpStatus, String message) {
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
