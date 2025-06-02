package com.reservemate.reserve_mate_backend.reservation.domain;

public enum ReservationStatus {
    PENDING             // 예약 신청 완료 (대기 중, 확정 아님)
    , CONFIRMED         // 예약 확정 (결제 후 사용 가능 상태)
    , CANCELED          // 취소 됨
    , COMPLETED         // 사용 후 완료 처리
}
