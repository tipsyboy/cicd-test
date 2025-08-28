package com.dabom.member.exception;

import com.dabom.common.exception.BaseException;
import com.dabom.common.exception.ExceptionType;
import lombok.Getter;

@Getter
public class MemberException extends BaseException {

    public MemberException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
