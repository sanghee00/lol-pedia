package com.example.lolpedia.global.exception;

import org.springframework.http.HttpStatus;

public record ErrorResponse(int status, String message) {

    public static ErrorResponse of(ErrorCode errorCode) {
        HttpStatus status = errorCode.getStatus();
        return new ErrorResponse(status.value(), errorCode.getMessage());
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        HttpStatus status = errorCode.getStatus();
        return new ErrorResponse(status.value(), message);
    }

}
