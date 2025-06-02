package com.reservemate.reserve_mate_backend.reservation.dto.request;

import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentMethod;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class ReservationPaymentRequest {

    private String orderId;
    private String paymentKey;
    private Integer amount;
    private Long reservationId;

    public Payment toEntity(Reservation reservation) {
        return new Payment(
            this.orderId, this.paymentKey, this.amount, reservation.getUser(), PaymentMethod.CARD, reservation);
    }

}
