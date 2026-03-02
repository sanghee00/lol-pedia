package com.example.lolpedia.matches.enums;

import com.example.lolpedia.global.success.SuccessCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum MatchSuccessCode implements SuccessCode {

    MATCH_SUCCESS_FOUND(HttpStatus.OK, "기록 조회 성공"),
    MATCH_INFO_SUCCESS_FOUND(HttpStatus.OK, "매치 상세조회 성공");

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
