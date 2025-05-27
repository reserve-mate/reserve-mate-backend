package com.reservemate.reserve_mate_backend.reservation.util;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.reservation.dto.request.ConfirmReservationRequest;
import com.reservemate.reserve_mate_backend.reservation.service.ReserveCUDService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReservationEvent {

    private final ReserveCUDService reserveCUDService;

    /* 결제 후 예약 확정 */
    @EventListener
    public void reservationConfirm(ConfirmReservationRequest reservationRequest) {
        reserveCUDService.reservationConfirm(reservationRequest);
    }

}
