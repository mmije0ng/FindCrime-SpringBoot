package com.backend.find_crime.apiPayload.exception.handler;


import com.backend.find_crime.apiPayload.code.BaseErrorCode;
import com.backend.find_crime.apiPayload.exception.GeneralException;

public class ErrorHandler extends GeneralException {
    public ErrorHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
