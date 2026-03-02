package com.example.lolpedia.playerhistory.enums;

import com.example.lolpedia.global.success.SuccessCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum PlayerHistorySuccessCode implements SuccessCode {

    PLAYER_HISTORY_SUCCESS_FOUND(HttpStatus.OK, "선수 기록 조회 성공");

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
