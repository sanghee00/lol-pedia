package com.example.lolpedia.player.exception;

import com.example.lolpedia.global.exception.BaseException;
import com.example.lolpedia.player.enums.PlayerErrorCode;
import lombok.Getter;

@Getter
public class PlayerException extends BaseException {

    private final PlayerErrorCode errorCode;

    public PlayerException(PlayerErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

}
