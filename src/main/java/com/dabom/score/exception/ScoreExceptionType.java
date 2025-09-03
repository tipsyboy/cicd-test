package com.dabom.score.exception;

import com.dabom.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public enum ScoreExceptionType implements ExceptionType {
    INVALID_ACCESS(HttpStatus.FORBIDDEN, "잘못된 접근 입니다."),
    ALREADY_RATED_CHANNEL(HttpStatus.CONFLICT, "이미 해당 채널에 평점을 매겼습니다."),
    ALREADY_RATED_VIDEO(HttpStatus.CONFLICT, "이미 해당 동영상에 평점을 매겼습니다."),
    SCORE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 평점을 찾을 수 없습니다."),
    INVALID_SCORE_RANGE(HttpStatus.BAD_REQUEST, "평점은 0.1에서 5.0 사이여야 합니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "평점자(member)를 찾을 수 없습니다."),
    TARGET_NOT_SPECIFIED(HttpStatus.BAD_REQUEST, "channel 또는 video 중 정확히 하나만 값이 있어야 합니다."),
    SCORE_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "평점 타입이 대상과 일치하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ScoreExceptionType(HttpStatus httpStatus, String message) {
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
