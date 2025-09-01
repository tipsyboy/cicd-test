package com.dabom.image.controller;

import com.dabom.common.exception.ErrorResponse;
import com.dabom.image.exception.ImageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ImageExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ImageException.class)
    public ResponseEntity<ErrorResponse> imageExceptionHandler(ImageException e) {
        log.info("[ImageException] ex : {}", e.getExceptionType().message());
        return ResponseEntity.ok(ErrorResponse.from(e));
    }
}
