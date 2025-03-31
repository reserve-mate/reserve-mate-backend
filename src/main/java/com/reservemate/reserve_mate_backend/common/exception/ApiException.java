package com.reservemate.reserve_mate_backend.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    private HttpStatus errorCode;

    public ApiException(HttpStatus errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
