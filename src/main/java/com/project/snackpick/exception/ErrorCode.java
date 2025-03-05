package com.project.snackpick.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "%s 처리 중 오류가 발생하였습니다."),
    NOT_PERMISSION(HttpStatus.UNAUTHORIZED, "%s 권한이 없습니다."),

    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "해당 카테고리 정보를 찾을 수 없습니다."),
    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "해당 회원 정보를 찾을 수 없습니다."),
    NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "해당 리뷰 정보를 찾을 수 없습니다."),
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "해당 제품 정보를 찾을 수 없습니다."),

    MEMBER_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    ALREADY_DELETE_REVIEW(HttpStatus.BAD_REQUEST, "이미 삭제된 리뷰입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    public String formatMessage(Object... args) {
        return String.format(this.message, args);
    }

}
