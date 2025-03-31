package com.reservemate.reserve_mate_backend.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ErrorDto {

    private HttpStatus errorCode;
    private String message;

    public static ResponseEntity<ErrorDto> toResponseEntity(ApiException ex) {
        ErrorDto errorDTO = ErrorDto.builder()
            .errorCode(ex.getErrorCode().getHttpStatus())
            .message(ex.getMessage())
            .build();

        return ResponseEntity.ok(errorDTO);
    }

}
