package com.reservemate.reserve_mate_backend.common.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private String errorCode;

    public ApiException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
