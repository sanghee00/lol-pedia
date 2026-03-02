package com.example.lolpedia.matches.enums;

import com.example.lolpedia.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum MatchErrorCode implements ErrorCode {

    MATCH_NOT_FOUND(HttpStatus.NOT_FOUND, "경기 기록을 찾지 못했습니다."),
    MATCH_PERIOD_INVALID(HttpStatus.BAD_REQUEST, "종료 날짜는 시작 날짜보다 빠를 수 없습니다.");

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
