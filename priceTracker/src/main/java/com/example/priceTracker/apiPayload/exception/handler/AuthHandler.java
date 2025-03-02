package com.example.priceTracker.apiPayload.exception.handler;

import com.example.priceTracker.apiPayload.code.BaseErrorCode;
import com.example.priceTracker.apiPayload.exception.GeneralException;

public class AuthHandler extends GeneralException {

    public AuthHandler(BaseErrorCode errorCode) { super(errorCode); }

}
