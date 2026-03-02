package com.example.lolpedia.player.enums;

import com.example.lolpedia.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum PlayerErrorCode implements ErrorCode {

    PLAYER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 플레이어"),
    INVALID_PAGE(HttpStatus.BAD_REQUEST, "존재하지 않는 플레이어 페이지"),
    PLAYER_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "플레이어 상세 데이터 조회 실패");

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
