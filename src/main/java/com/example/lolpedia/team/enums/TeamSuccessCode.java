package com.example.lolpedia.team.enums;

import com.example.lolpedia.global.success.SuccessCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum TeamSuccessCode implements SuccessCode {

    TEAM_SUCCESS_FOUND(HttpStatus.OK, "팀 조회 성공");

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
