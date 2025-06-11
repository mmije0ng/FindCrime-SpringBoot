package com.backend.find_crime.apiPayload.code;

public interface BaseErrorCode {

    ErrorReasonDTO getReason();

    ErrorReasonDTO getReasonHttpStatus();
}