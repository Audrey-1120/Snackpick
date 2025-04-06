package com.project.snackpick.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
@RequiredArgsConstructor
public class ErrorResponse {

    private final HttpStatus httpStatus;
    private final String code;
    private final boolean success;
    private final String message;

    public static ResponseEntity<ErrorResponse> error(CustomException e) {
        return ResponseEntity
                .status(e.getErrorCode().getHttpStatus())
                .body(ErrorResponse.builder()
                        .success(false)
                        .message(getErrorMessage(e))
                        .build());
    }

    private static String getErrorMessage(CustomException e) {
        return (e.getMessage() != null && !e.getMessage().isEmpty())
                ? e.getMessage()
                : e.getErrorCode().getMessage();
    }
}