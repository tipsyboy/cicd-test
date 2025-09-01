package com.dabom.image.exception;

import com.dabom.common.exception.BaseException;
import com.dabom.common.exception.ExceptionType;

public class ImageException extends BaseException {
    public ImageException(ExceptionType message) {
        super(message);
    }
}
