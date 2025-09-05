package com.dabom.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    // Security에서 로그인 실패시 터지는 에러
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseEntity<ErrorResponse> securityLoginErrorHandler(InternalAuthenticationServiceException e) {
        log.error("[InternalAuthenticationServiceException] 회원 목록에 없는 로그인: {}", e.getMessage(), e);
        return ResponseEntity.ok(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "아이디 혹은 비밀번호가 잘 못 입력되었습니다. 다시 로그인해주세요"));
    }

    // Dto 검증 핸들러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> securityLoginErrorHandler(MethodArgumentNotValidException e) {
        log.error("[MethodArgumentNotValidException] 입력값 검증 실패: {}", e.getMessage(), e);
        return ResponseEntity.ok(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "잘못된 입력값이 존재합니다. 다시 입력해주세요"));
    }

    // 이외의 모든 예외를 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> globalExceptionHandler(Exception e) {
        log.error("[Exception] 예기치 못한 예외가 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다"));
    }
}
