package com.project.snackpick.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "%s 처리 중 오류가 발생하였습니다."),
    FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "%s 이미지 업로드 중 오류가 발생하였습니다."),
    NOT_PERMISSION(HttpStatus.FORBIDDEN, "%s 권한이 없습니다."),
    NOT_FOUND_ENTITY(HttpStatus.NOT_FOUND, "해당 %s 정보를 찾을 수 없습니다."),
    MEMBER_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    ALREADY_DELETE(HttpStatus.BAD_REQUEST, "이미 삭제된 %s입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public String formatMessage(Object... args) {
        return String.format(this.message, args);
    }

}
