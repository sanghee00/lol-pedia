package com.example.lolpedia.playerhistory.exception;

import com.example.lolpedia.global.exception.BaseException;
import com.example.lolpedia.playerhistory.enums.PlayerHistoryErrorCode;

public class PlayerHistoryException extends BaseException {

    public PlayerHistoryException(PlayerHistoryErrorCode errorCode) {
        super(errorCode);
    }

}
