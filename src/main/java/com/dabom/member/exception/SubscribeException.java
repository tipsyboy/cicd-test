package com.dabom.member.exception;

import com.dabom.common.exception.BaseException;
import com.dabom.common.exception.ExceptionType;
import lombok.Getter;

@Getter
public class SubscribeException extends BaseException {

    public SubscribeException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
