package com.example.lolpedia.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ErrorResponse> handleBaseException(BaseException e, HttpServletRequest request) {
        ErrorCode errorCode = e.getErrorCode();

        log.error("BaseException 발생 | Code: {}, URL: {}, Message: {}",
            errorCode,
            request.getRequestURI(),
            e.getMessage(),
            e
        );

        return ResponseEntity.status(errorCode.getStatus())
            .body(new ErrorResponse(errorCode.getStatus().value(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.joining(", "));

        log.warn("입력값 검증 실패 | URL: {}, Message: {}", request.getRequestURI(), message);

        return ResponseEntity.badRequest()
            .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String message = "잘못된 파라미터 타입입니다. [" + e.getName() + "]";

        log.warn("파라미터 타입 불일치 | URL: {}, Param: {}, Value: {}",
            request.getRequestURI(), e.getName(), e.getValue());

        return ResponseEntity.badRequest()
            .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception e, HttpServletRequest request) {
        log.error("처리되지 않은 예외 발생 | URL: {}", request.getRequestURI(), e);

        return ResponseEntity.internalServerError()
            .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다."));
    }

}
