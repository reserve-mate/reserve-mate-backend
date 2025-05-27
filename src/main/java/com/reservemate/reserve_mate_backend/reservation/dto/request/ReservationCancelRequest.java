package com.reservemate.reserve_mate_backend.reservation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ReservationCancelRequest {

    private Long reservationId;
    private String cancelReason;

}
