package com.reservemate.reserve_mate_backend.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ErrorDto> handleCustomException(ApiException ex) {
        return ErrorDto.toResponseEntity(ex);
    }

}
