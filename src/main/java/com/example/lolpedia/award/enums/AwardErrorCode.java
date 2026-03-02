package com.example.lolpedia.award.enums;

import com.example.lolpedia.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum AwardErrorCode implements ErrorCode {

    AWARD_NOT_FOUND(HttpStatus.NOT_FOUND, "수상 기록을 조회하지 못했습니다.");

    private final HttpStatus status;
    private final String message;

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
