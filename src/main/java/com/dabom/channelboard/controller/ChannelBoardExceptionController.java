package com.dabom.channelboard.controller;

import com.dabom.channelboard.exception.ChannelBoardException;
import com.dabom.common.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ChannelBoardExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ChannelBoardException.class)
    public ResponseEntity<ErrorResponse> channelBoardExceptionHandler(ChannelBoardException e) {
        log.info("[ChannelBoardException] ex : {}", e.getExceptionType().message());
        return ResponseEntity.ok(ErrorResponse.from(e));
    }
}
