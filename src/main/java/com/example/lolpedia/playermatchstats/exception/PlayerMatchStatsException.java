package com.example.lolpedia.playermatchstats.exception;

import com.example.lolpedia.global.exception.BaseException;
import com.example.lolpedia.playermatchstats.enums.PlayerMatchStatsErrorCode;

public class PlayerMatchStatsException extends BaseException {
    public PlayerMatchStatsException(PlayerMatchStatsErrorCode errorCode) {
        super(errorCode);
    }
}
