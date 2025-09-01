package com.dabom.chat.controller;

import com.dabom.chat.exception.ChatException;
import com.dabom.common.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ChatExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChatException.class)
    public ResponseEntity<ErrorResponse> chatExceptionHandler(ChatException e) {
        log.info("[ChatException] ex : {}", e.getExceptionType().message());
        return ResponseEntity.ok(ErrorResponse.from(e));
    }
}
