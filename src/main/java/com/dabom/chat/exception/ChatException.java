package com.dabom.chat.exception;

import com.dabom.common.exception.BaseException;
import com.dabom.common.exception.ExceptionType;

public class ChatException extends BaseException {
    public ChatException(ExceptionType message) {
        super(message);
    }
}
