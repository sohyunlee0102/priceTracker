package com.example.priceTracker.apiPayload.exception;

import com.example.priceTracker.apiPayload.code.BaseErrorCode;
import com.example.priceTracker.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {

    private BaseErrorCode code;

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorHttpStatus() {
        return this.code.getReasonHttpStatus();
    }

}
