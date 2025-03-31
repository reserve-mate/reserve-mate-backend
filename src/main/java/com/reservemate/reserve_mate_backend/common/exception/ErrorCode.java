package com.reservemate.reserve_mate_backend.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // common
    INVALID_INPUT_VALUE(BAD_REQUEST, "유효하지 않은 입력값입니다."), SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 오류"), USER_NOT_FOUND(
        UNAUTHORIZED, "유저 정보를 찾을 수 없습니다"), UNAUTHORIZED_CODE(UNAUTHORIZED, "인증 정보가 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
