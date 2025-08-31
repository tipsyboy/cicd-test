package com.dabom.boardcomment.exception;

import com.dabom.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum BoardCommentExceptionType implements ExceptionType {


    COMMENT_NOT_FOUND(NOT_FOUND, "댓글을 찾을 수 없습니다."),
    BOARD_NOT_FOUND_FOR_COMMENT(NOT_FOUND, "댓글을 작성할 게시글을 찾을 수 없습니다."),


    COMMENT_ACCESS_DENIED(FORBIDDEN, "댓글 접근 권한이 없습니다."),
    COMMENT_UPDATE_ACCESS_DENIED(FORBIDDEN, "댓글 수정 권한이 없습니다."),
    COMMENT_DELETE_ACCESS_DENIED(FORBIDDEN, "댓글 삭제 권한이 없습니다."),


    COMMENT_CONTENT_EMPTY(BAD_REQUEST, "댓글 내용을 입력해주세요."),
    COMMENT_CONTENT_TOO_LONG(BAD_REQUEST, "댓글 내용이 너무 깁니다."),
    INVALID_COMMENT_FORMAT(BAD_REQUEST, "댓글 형식이 올바르지 않습니다."),


    COMMENT_ALREADY_DELETED(CONFLICT, "이미 삭제된 댓글입니다."),
    BOARD_CLOSED_FOR_COMMENT(CONFLICT, "댓글 작성이 불가능한 게시글입니다."),


    INVALID_SORT_PARAMETER(BAD_REQUEST, "올바르지 않은 정렬 방식입니다."),
    INVALID_PAGE_PARAMETER(BAD_REQUEST, "페이지 번호가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    BoardCommentExceptionType(HttpStatus httpStatus, String message) {
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
