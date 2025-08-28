package com.dabom.video.exception;

import com.dabom.common.exception.BaseException;
import com.dabom.common.exception.ExceptionType;

public class VideoException extends BaseException {

    public VideoException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
