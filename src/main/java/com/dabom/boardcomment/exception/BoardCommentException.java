package com.dabom.boardcomment.exception;

import com.dabom.common.exception.BaseException;
import com.dabom.common.exception.ExceptionType;

public class BoardCommentException extends BaseException {
    public BoardCommentException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
