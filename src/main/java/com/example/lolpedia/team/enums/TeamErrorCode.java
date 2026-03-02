package com.example.lolpedia.team.enums;

import com.example.lolpedia.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum TeamErrorCode implements ErrorCode {

    TEAM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 팀"),
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "존재하지 않는 팀 페이지");;

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
