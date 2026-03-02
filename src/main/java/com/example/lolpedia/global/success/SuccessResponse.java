package com.example.lolpedia.global.success;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

public record SuccessResponse<T>(
        int status,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T result
) {

    public static <T> SuccessResponse<T> of(SuccessCode successCode) {
        HttpStatus status = successCode.getStatus();
        return new SuccessResponse<>(status.value(), successCode.getMessage(), null);
    }

    public static <T> SuccessResponse<T> of(SuccessCode successCode, T result) {
        HttpStatus status = successCode.getStatus();
        return new SuccessResponse<>(status.value(), successCode.getMessage(), result);
    }
}
