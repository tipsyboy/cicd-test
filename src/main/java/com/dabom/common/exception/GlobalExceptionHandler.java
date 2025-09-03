package com.dabom.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> baseExceptionHandler(BaseException e) {
        String errorClassName = e.getClass().getSimpleName();
        int httpStatusCode = e.getExceptionType().statusCode().value();
        String errorMessage = e.getExceptionType().message();

        log.error("[{}] >> {}", errorClassName, errorMessage);
        return ResponseEntity.ok(ErrorResponse.of(httpStatusCode, errorMessage));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> togetherExceptionHandler(HttpMessageNotReadableException e) {
        log.info("[HttpMessageNotReadableException] ex : {}", e.getMessage());
        return ResponseEntity.ok(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "숫자를 입력해주세요"));
    }

    // 이외의 모든 예외를 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> globalExceptionHandler(Exception e) {
        log.error("[Exception] 예기치 못한 예외가 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다"));
    }
}
