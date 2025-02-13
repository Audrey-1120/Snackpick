package com.project.snackpick.handler;

import com.project.snackpick.exception.CustomException;
import com.project.snackpick.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // Controller 에서 발생하는 예외를 자동으로 처리한다.
public class GlobalExceptionHandler {

    // CustomException 이 발생하면 ErrorResponse.error()를 호출한다.
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return ErrorResponse.error(e);
    }
}
