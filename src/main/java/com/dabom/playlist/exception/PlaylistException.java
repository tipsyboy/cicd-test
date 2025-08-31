package com.dabom.playlist.exception;

import com.dabom.common.exception.BaseException;
import com.dabom.common.exception.ExceptionType;

public class PlaylistException extends BaseException {
    public PlaylistException(ExceptionType message) {
        super(message);
    }
}
