package com.project.snackpick.handler;

import com.project.snackpick.exception.CustomException;
import com.project.snackpick.exception.ErrorCode;
import com.project.snackpick.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {

        ErrorCode code = e.getErrorCode();

        ErrorResponse response = ErrorResponse.builder()
                .status(code.getHttpStatus().value())
                .code(code.name())
                .success(false)
                .message(getClientMessage(e))
                .build();

        return ResponseEntity.status(code.getHttpStatus()).body(response);
    }

    private String getClientMessage(CustomException e) {
        ErrorCode code = e.getErrorCode();
        String msg = e.getMessage();

        return code.isExposeToClient()
                ? (msg != null && !msg.isEmpty()
                ? msg
                : code.getMessage())
                : "요청 처리 중 문제가 발생하였습니다.";
    }
}
