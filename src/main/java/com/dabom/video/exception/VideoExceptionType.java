package com.dabom.video.exception;

import com.dabom.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public enum VideoExceptionType implements ExceptionType {

    MISSING_FILE_EXTENSION(BAD_REQUEST, "파일 확장자가 없습니다."),
    FILE_SIZE_EXCEEDED(BAD_REQUEST, "파일 크기는 100MB를 초과할 수 없습니다."),
    INVALID_CONTENT_TYPE(BAD_REQUEST, "비디오 파일만 업로드 가능합니다."),
    UNSUPPORTED_VIDEO_FORMAT(BAD_REQUEST, "지원하지 않는 비디오 형식입니다."),

    PERMISSION_DENIED(FORBIDDEN, "권한이 없는 요청입니다."),

    VIDEO_NOT_FOUND(NOT_FOUND, "해당 영상을 찾을 수 없습니다."),

    VIDEO_ALREADY_RATED(CONFLICT, "이미 평가 완료된 영상입니다");


    private final HttpStatus httpStatus;
    private final String message;

    VideoExceptionType(HttpStatus httpStatus, String message) {
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
