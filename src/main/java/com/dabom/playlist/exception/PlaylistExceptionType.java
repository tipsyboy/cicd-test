package com.dabom.playlist.exception;

import com.dabom.common.exception.ExceptionType;
import org.springframework.http.HttpStatus;

public enum PlaylistExceptionType implements ExceptionType {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    PLAYLIST_NOT_FOUND(HttpStatus.NOT_FOUND, "플레이리스트를 찾을 수 없습니다."),
    VIDEO_NOT_FOUND(HttpStatus.NOT_FOUND, "비디오를 찾을 수 없습니다."),
    NO_PERMISSION_ADD_VIDEO(HttpStatus.FORBIDDEN, "영상을 추가할 권한이 없습니다."),
    VIDEO_ALREADY_IN_PLAYLIST(HttpStatus.CONFLICT, "이미 플레이리스트에 추가된 영상입니다."),
    NO_PERMISSION_DELETE_PLAYLIST(HttpStatus.FORBIDDEN, "플레이리스트를 삭제할 권한이 없습니다."),
    NO_PERMISSION_VIEW_PLAYLIST(HttpStatus.FORBIDDEN, "플레이리스트를 조회할 권한이 없습니다."),
    NO_PERMISSION_UPDATE_PLAYLIST(HttpStatus.FORBIDDEN, "플레이리스트를 수정할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    PlaylistExceptionType(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus statusCode() {
        return httpStatus;
    }

    @Override
    public String message() {
        return message;
    }
}
