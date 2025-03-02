package com.example.priceTracker.apiPayload.exception.handler;

import com.example.priceTracker.apiPayload.code.BaseErrorCode;
import com.example.priceTracker.apiPayload.exception.GeneralException;

public class UserHandler extends GeneralException {

    public UserHandler(BaseErrorCode errorCode) { super(errorCode); }

}
