package com.test.auth.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionManager {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> appExceptionHandler(AppException appException) {
        return ResponseEntity.status(appException.getErrorCode().getHttpStatus())
                .body(appException.getErrorCode().name() + " " + appException.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> runtimeExceptionHandler(RuntimeException runtimeException) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(runtimeException.getMessage());
    }

}
