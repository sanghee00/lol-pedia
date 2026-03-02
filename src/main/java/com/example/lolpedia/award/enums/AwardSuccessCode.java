package com.example.lolpedia.award.enums;

import com.example.lolpedia.global.success.SuccessCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum AwardSuccessCode implements SuccessCode {

    AWARD_SUCCESS_FOUND(HttpStatus.OK, "수상기록 조회 성공");

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
