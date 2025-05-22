package com.reservemate.reserve_mate_backend.common.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.GONE;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // common
    INVALID_INPUT_VALUE(BAD_REQUEST, "유효하지 않은 입력값입니다."), SERVER_ERROR(INTERNAL_SERVER_ERROR, "서버 오류"), USER_NOT_FOUND(
        UNAUTHORIZED, "유저 정보를 찾을 수 없습니다"), UNAUTHORIZED_CODE(UNAUTHORIZED, "인증 정보가 올바르지 않습니다."), ADMIN_FORBIDDEN(
            FORBIDDEN, "관리자 권한이 아닙니다."), GONE_DATA(GONE, "이미 삭제된 데이터입니다."), MISSING_QUERY_PARAM(BAD_REQUEST,
                "요청에 필수 파라미터가 누락되었습니다. 값을 확인해주세요.")
    // 매치 관련 에러 처리
    , EXIST_MATCH_ERROR(CONFLICT, "중복된 매치가 존재합니다."), EXIST_MATCH_TIME_ERROR(CONFLICT,
        "겹치는 시간대에 매치가 존재합니다."), END_MATCH_ERROR(BAD_REQUEST, "이미 진행중 또는는 종료된 매치입니다."), FINISH_MATCH_ERROR(BAD_REQUEST,
            "이미 인원이 마감된 매치입니다."), NO_MATCH_ERROR(BAD_REQUEST, "매치가 정보 존재하지 않습니다."), INVALID_PRICE(BAD_REQUEST,
                "가격이 일치하지 않습니다."), MANAGER_ALREADY_ASSIGNED(BAD_REQUEST,
                    "해당 시간대에 이미 다른 코트에 매니저가 배정되어 있습니다."), NON_DELETABLE(BAD_REQUEST,
                        "해당 매치는 삭제할 수 없는 상태입니다."), NOT_ONGOING_MATCH(BAD_REQUEST,
                            "진행중인 매치가 아닙니다."), ALREADY_ONGOING_MATCH(BAD_REQUEST,
                                "이미 진행중인 매치입니다."), INVALID_MATCH_STATE_TRANSITION(BAD_REQUEST,
                                    "모집이 완료되거나 참가자 수가 과반수가 넘은 매치만 시작할 수 있습니다."), INVALID_MATCH_STATE_CLOSE_TO_DEADLINE(
                                        BAD_REQUEST,
                                        "마감 임박 상태의 참가인원 과반수가 넘은 매치만 모집 마감 처리할 수 있습니다."), NOT_AVAILABLE_STAT_CHG(
                                            BAD_REQUEST, "해당 상태에서는 상태를 변경할 수 없습니다."), MATCH_NOT_STARTED_YET(BAD_REQUEST,
                                                "아직 매치 시작 시간이 되지 않아 상태를 변경할 수 없습니다."), MAX_TEAM_SIZE_CONFLICT(CONFLICT,
                                                    "현재 참가 인원보다 작은 최대 팀원 수로 변경할 수 없습니다."), UPDATE_NOT_ALLOWED_MATCH(
                                                        CONFLICT, "수정이 불가능한 상태의 매치입니다."), MATCH_NOT_MANAGER(BAD_REQUEST,
                                                            "매치의 매니저만 수행할 수 있는 작업입니다."), MATCH_TIME_ALREADY_PASSED(
                                                                BAD_REQUEST, "이미 지난 시간의 매치는 등록할 수 없습니다.")

    // 매치 플레이어 관련 에러 처리
    , EXIST_MATCH_PLAYER_ERROR(CONFLICT, "이미 해당 매치에 신청한 이력이 존재합니다."), NOT_FOUND_PLAYER(BAD_REQUEST,
        "매치 신청이력이 존재하지 않습니다."), ALREADY_CANCEL_PLAYER(BAD_REQUEST, "이미 취소된 매치입니다."), ALREADY_COMPLETE_PLAYER(
            BAD_REQUEST, "이미 참여했던 이력이 있는 매치입니다."), ALREADY_ONGOING_PLAYER(BAD_REQUEST,
                "이미 진행중인 매치입니다."), MATCH_HAS_PARTICIPANTS(BAD_REQUEST,
                    "이미 참가자가 존재하여 이 매치를 삭제할 수 없습니다."), NOT_ONGOING_PLAYER(BAD_REQUEST, "현재 진행중인 플레이어가 아닙니다.")

    // 결제 관련 에러
    , PAYMETN_ERROR(INTERNAL_SERVER_ERROR, "결제처리가 정상적으로 처리되지 않았습니다."), PAYMENT_FAILED(BAD_REQUEST,
        "결제처리에 실패했습니다."), NOT_FOUND_PAYMENT(BAD_REQUEST, "결제 정보가 존재하지 않습니다."), DUPLICATION_PAYMENT(BAD_REQUEST,
            "이미 결제 요청한 이력이 존재합니다."), DUPLICATION_PAYMENT_CONFIRM(BAD_REQUEST, "이미 결제를 승인한 이력이 존재합니다."), NOT_PAID(
                BAD_REQUEST, "결제된 이력이 존재하지 않습니다."), PAYMENT_AMOUNT_MISMATCH(BAD_REQUEST,
                    "결제 금액이 일치하지 않습니다."), NOT_READY_PAYEMNT(BAD_REQUEST, "결제 요청된 이력이 존재하지 않습니다."), NOT_CANCEL_PAYMENT(
                        BAD_REQUEST, "아직 취소된 결제가 아닙니다. 결제 상태를 확인해주세요."), PAYMETN_CANCEL_ERROR(INTERNAL_SERVER_ERROR,
                            "결제 취소 처리가 정상적으로 처리되지 않았습니다.")

    // 예약 관련 에러
    , NO_AVAILABLE_TIME_ON_DAY(BAD_REQUEST, "선택하신 날짜에는 예약 가능한 시간이 존재하지 않습니다."), PAST_TIME_NOT_ALLOWED(BAD_REQUEST,
        "이미 지난 시간에는 예약할 수 없습니다."), DUPLICATE_RESERVATION(BAD_REQUEST,
            "이미 해당 시간에 예약이 존재합니다."), DUPLICATE_ACTIVE_RESERVATION(BAD_REQUEST,
                "중복된 대기 또는 확정 예약이 존재합니다."), NOT_FOUND_RESERVATION(BAD_REQUEST, "예약 정보가 존재하지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

}
