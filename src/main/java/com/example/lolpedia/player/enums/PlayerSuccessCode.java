package com.example.lolpedia.player.enums;

import com.example.lolpedia.global.success.SuccessCode;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
public enum PlayerSuccessCode implements SuccessCode {

    PLAYER_SUCCESS_FOUND(HttpStatus.OK, "플레이어 조회 성공"),
    PLAYER_INFO_SUCCESS_FOUND(HttpStatus.OK, "플레이어 상세 정보 조회 성공");

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
