package com.dabom.score.controller;

import com.dabom.common.exception.ErrorResponse;
import com.dabom.score.exception.ScoreException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ScoreExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ScoreException.class)
    public ResponseEntity<ErrorResponse> scoreExceptionHandler(ScoreException e) {
        log.info("[ScoreException] ex : {}", e.getExceptionType().message());
        return ResponseEntity.ok(ErrorResponse.from(e));
    }
}
