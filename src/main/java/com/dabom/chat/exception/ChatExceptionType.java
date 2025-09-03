package com.dabom.chat.exception;

import com.dabom.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public enum ChatExceptionType implements ExceptionType {

    CHAT_ROOM_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 채팅방을 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "해당 채팅방에 대한 권한이 없습니다."),
    SENDER_NOT_FOUND(HttpStatus.NOT_FOUND, "메시지 발신자 정보를 찾을 수 없습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 멤버를 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ChatExceptionType(HttpStatus httpStatus, String message) {
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
