package com.dabom.common;

import com.dabom.common.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> togetherExceptionHandler(HttpMessageNotReadableException e) {
        log.info("[HttpMessageNotReadableException] ex : {}", e.getMessage());
        return ResponseEntity.ok(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), "숫자를 입력해주세요"));
    }
}
