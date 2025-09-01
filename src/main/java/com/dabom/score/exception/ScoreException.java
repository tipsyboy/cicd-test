package com.dabom.score.exception;

import com.dabom.common.exception.BaseException;
import com.dabom.common.exception.ExceptionType;

public class ScoreException extends BaseException {
    public ScoreException(ExceptionType message) {
        super(message);
    }
}
