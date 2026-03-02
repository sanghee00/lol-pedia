package com.example.lolpedia.matches.exception;

import com.example.lolpedia.global.exception.BaseException;
import com.example.lolpedia.matches.enums.MatchErrorCode;

public class MatchException extends BaseException {

    public MatchException(MatchErrorCode errorCode) {
        super(errorCode);
    }

}
