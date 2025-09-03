package com.dabom.channelboard.exception;

import com.dabom.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum ChannelBoardExceptionType implements ExceptionType {

    // NOT_FOUND (404) - 리소스를 찾을 수 없음
    BOARD_NOT_FOUND(NOT_FOUND, "게시글을 찾을 수 없습니다."),
    CHANNEL_NOT_FOUND(NOT_FOUND, "채널을 찾을 수 없습니다."),

    // FORBIDDEN (403) - 권한 없음
    BOARD_ACCESS_DENIED(FORBIDDEN, "게시글 접근 권한이 없습니다."),
    BOARD_UPDATE_ACCESS_DENIED(FORBIDDEN, "게시글 수정 권한이 없습니다."),
    BOARD_DELETE_ACCESS_DENIED(FORBIDDEN, "게시글 삭제 권한이 없습니다."),

    // BAD_REQUEST (400) - 잘못된 요청
    BOARD_TITLE_EMPTY(BAD_REQUEST, "게시글 제목을 입력해주세요."),
    BOARD_TITLE_TOO_LONG(BAD_REQUEST, "게시글 제목이 너무 깁니다."),
    BOARD_CONTENT_EMPTY(BAD_REQUEST, "게시글 내용을 입력해주세요."),
    BOARD_CONTENT_TOO_LONG(BAD_REQUEST, "게시글 내용이 너무 깁니다."),
    INVALID_BOARD_FORMAT(BAD_REQUEST, "게시글 형식이 올바르지 않습니다."),
    INVALID_SORT_PARAMETER(BAD_REQUEST, "올바르지 않은 정렬 방식입니다."),
    INVALID_PAGE_PARAMETER(BAD_REQUEST, "페이지 번호가 올바르지 않습니다."),
    INVALID_SIZE_PARAMETER(BAD_REQUEST, "페이지 크기가 올바르지 않습니다."),

    // 403
    UNAUTHORIZED_ACCESS(HttpStatus.FORBIDDEN, "해당 게시글에 대한 권한이 없습니다."),

    // CONFLICT (409) - 비즈니스 로직 충돌
    BOARD_ALREADY_DELETED(CONFLICT, "이미 삭제된 게시글입니다."),
    CHANNEL_CLOSED_FOR_BOARD(CONFLICT, "게시글 작성이 불가능한 채널입니다.");


    private final HttpStatus httpStatus;
    private final String message;

    ChannelBoardExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus statusCode() {
        return this.httpStatus;
    }

    @Override
    public String message() {
        return this.message;
    }
}