package com.dabom.channelboard.exception;

import com.dabom.common.exception.BaseException;
import com.dabom.common.exception.ExceptionType;

public class ChannelBoardException extends BaseException {
    public ChannelBoardException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}