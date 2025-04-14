package com.reservemate.reserve_mate_backend.common.exception;

import lombok.Getter;

@Getter
public class TossApiException extends RuntimeException {

    private String tossErrorCode;
    private String tossErrorMsg;

    public TossApiException(String tossErrorCode, String tossErrorMsg) {
        super(tossErrorMsg);
        this.tossErrorCode = tossErrorCode;
    }

}
