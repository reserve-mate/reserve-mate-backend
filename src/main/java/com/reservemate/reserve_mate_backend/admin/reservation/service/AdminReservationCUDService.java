package com.reservemate.reserve_mate_backend.admin.reservation.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;
import com.reservemate.reserve_mate_backend.reservation.dto.request.ReservationCancelRequest;
import com.reservemate.reserve_mate_backend.reservation.repository.ReserveRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReservationCUDService {

    private final ReserveRepository reserveRepository;
    private final ApplicationEventPublisher eventPublisher;

    /* 관리자 예약 상태 변경 */
    @Transactional
    public void adminReservationCancel(Long reservationId, ReservationStatus reservationStatus) {
        Reservation reservation = reserveRepository.findById(reservationId).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_RESERVATION));
        reservation.isCompleteOrCancel();

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            eventPublisher.publishEvent(new ReservationCancelRequest(reservationId, "사용자 요구에 의한 취소 요청"));
        }
        reservation.cancel("관리자에 의한 취소");

    }

}
