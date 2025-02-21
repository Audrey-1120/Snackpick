package com.project.snackpick.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."),
    MEMBER_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
