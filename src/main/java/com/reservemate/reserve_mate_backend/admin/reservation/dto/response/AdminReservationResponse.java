package com.reservemate.reserve_mate_backend.admin.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AdminReservationResponse {

    private Long reservationId;
    private String userName;
    private String facilityName;
    private String courtName;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private ReservationStatus reservationStatus;
    private Integer totalPrice;

}
