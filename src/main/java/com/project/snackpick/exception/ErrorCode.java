package com.project.snackpick.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    NOT_PERMISSION(HttpStatus.FORBIDDEN, "%s 권한이 없습니다.", true),
    NOT_FOUND_ENTITY(HttpStatus.NOT_FOUND, "요청하신 %s 정보를 찾을 수 없습니다.", true),
    ALREADY_DELETE(HttpStatus.BAD_REQUEST, "이미 삭제된 %s입니다.", true),

    // 댓글
    COMMENT_PROCESSING_ERROR(HttpStatus.BAD_REQUEST, "댓글 처리 중 문제가 발생하였습니다.", false),

    // 리뷰
    REVIEW_PROCESSING_ERROR(HttpStatus.BAD_REQUEST, "리뷰 처리 중 문제가 발생하였습니다.", false),
    REVIEW_IMAGE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "리뷰 이미지 업로드 중 오류가 발생하였습니다.", false),
    REVIEW_IMAGE_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "리뷰 이미지 삭제 중 오류가 발생하였습니다.", false),

    // 회원
    MEMBER_PROCESSING_ERROR(HttpStatus.BAD_REQUEST, "회원 정보 처리 중 문제가 발생하였습니다.", false),
    MEMBER_DUPLICATE(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다.", true),
    PROFILE_IMAGE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 이미지 업로드 중 오류가 발생하였습니다.", false),
    PROFILE_IMAGE_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 이미지 삭제 중 문제가 발생하였습니다.", false),

    // 제품
    PRODUCT_PROCESSING_ERROR(HttpStatus.BAD_REQUEST, "제품 정보 처리 중 문제가 발생하였습니다.", false);

    private final HttpStatus httpStatus;
    private final String message;
    private final boolean exposeToClient;

    public String formatMessage(Object... args) {
        return String.format(this.message, args);
    }
}
