package com.example.lolpedia.award.exception;

import com.example.lolpedia.award.enums.AwardErrorCode;
import com.example.lolpedia.global.exception.BaseException;

public class AwardException extends BaseException {

    public AwardException(AwardErrorCode message) {
        super(message);
    }

}
