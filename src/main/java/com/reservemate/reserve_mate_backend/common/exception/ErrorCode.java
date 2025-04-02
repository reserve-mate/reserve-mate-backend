package com.reservemate.reserve_mate_backend.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.CONFLICT;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // common
    INVALID_INPUT_VALUE(BAD_REQUEST, "유효하지 않은 입력값입니다."), SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 오류"), USER_NOT_FOUND(
        UNAUTHORIZED, "유저 정보를 찾을 수 없습니다"), UNAUTHORIZED_CODE(UNAUTHORIZED, "인증 정보가 올바르지 않습니다.")

    // 매치 관련 에러 처리
    , EXIST_MATCH_ERROR(CONFLICT, "중복된 매치가 존재합니다."), EXIST_MATCH_TIME_ERROR(CONFLICT,
        "겹치는 시간대에 매치가 존재합니다."), END_MATCH_ERROR(BAD_REQUEST, "이미 진행중 또는는 종료된 매치입니다."), FINISH_MATCH_ERROR(BAD_REQUEST,
            "이미 인원이 마감된 매치입니다."), NO_MATCH_ERROR(BAD_REQUEST, "매치가 정보 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
