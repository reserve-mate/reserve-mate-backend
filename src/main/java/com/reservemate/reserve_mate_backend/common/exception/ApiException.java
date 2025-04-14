package com.reservemate.reserve_mate_backend.common.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private ErrorCode errorCode;

    public ApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

}
