package com.reservemate.reserve_mate_backend.payment.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReservationPaymentSuccessDto extends PaymentResponse {

    private Long reservationId;
    private String reservationNumber;
    private String facilityName;
    private String courtName;
    private LocalDate reserveDate;
    private LocalTime startTime;
    private LocalTime endTime;

    public ReservationPaymentSuccessDto(String status, Reservation reservation) {
        super(status);
        this.reservationId = reservation.getId();
        this.reservationNumber = reservation.getReservationNumber();
        this.facilityName = reservation.getCourt().getFacility().getName();
        this.courtName = reservation.getCourt().getName();
        this.reserveDate = reservation.getReserveDate();
        this.startTime = reservation.getStartTime();
        this.endTime = reservation.getEndTime();
    }

}
