package com.test.auth.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    USERNAME_DUPLICATED(HttpStatus.CONFLICT,""),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND,""),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,"");



    private HttpStatus httpStatus;
    private String message;
}
