package com.example.lolpedia.playermatchstats.enums;

import com.example.lolpedia.global.success.SuccessCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum PlayerMatchStatsSuccessCode implements SuccessCode {

    PLAYER_MATCH_STATS_SUCCESS_FOUND(HttpStatus.OK, "선수 경기 통계 기록 조회 성공");

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
