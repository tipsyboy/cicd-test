package com.dabom.together.controller;

import com.dabom.common.exception.ErrorResponse;
import com.dabom.together.exception.TogetherException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class TogetherExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(TogetherException.class)
    public ResponseEntity<ErrorResponse> togetherExceptionHandler(TogetherException e) {
        log.info("[TogetherException] ex : {}", e.getExceptionType().message());
        return ResponseEntity.ok(ErrorResponse.from(e));
    }
}
