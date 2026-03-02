package com.example.lolpedia.playermatchstats.enums;

import com.example.lolpedia.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum PlayerMatchStatsErrorCode implements ErrorCode {

    PLAYER_MATCH_STATS_NOT_FOUND(HttpStatus.NOT_FOUND, "선수 경기 통계 기록을 찾지 못했습니다.");

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
