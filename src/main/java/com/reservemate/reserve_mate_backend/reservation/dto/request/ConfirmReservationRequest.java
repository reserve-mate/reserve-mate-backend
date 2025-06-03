package com.reservemate.reserve_mate_backend.reservation.dto.request;

import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ConfirmReservationRequest {

    private Reservation reservation;

}
