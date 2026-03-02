package com.example.lolpedia.team.exception;

import com.example.lolpedia.global.exception.BaseException;
import com.example.lolpedia.team.enums.TeamErrorCode;

public class TeamException extends BaseException {

    public TeamException(TeamErrorCode errorCode) {
        super(errorCode);
    }

}
