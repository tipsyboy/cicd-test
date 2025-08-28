package com.dabom.together.exception;

import com.dabom.common.exception.BaseException;
import com.dabom.common.exception.ExceptionType;

public class TogetherException extends BaseException {
    public TogetherException(ExceptionType message) {
        super(message);
    }
}
