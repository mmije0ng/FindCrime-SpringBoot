package com.backend.findcrime.apiPayload.exception.handler;


import com.backend.findcrime.apiPayload.code.BaseErrorCode;
import com.backend.findcrime.apiPayload.exception.GeneralException;

public class ErrorHandler extends GeneralException {
    public ErrorHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
