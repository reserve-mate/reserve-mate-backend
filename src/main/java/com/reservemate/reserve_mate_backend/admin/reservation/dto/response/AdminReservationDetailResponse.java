package com.reservemate.reserve_mate_backend.admin.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentStatus;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)  // 
@Getter
@Setter
public class AdminReservationDetailResponse {

    private Long reservationId;
    private ReservationStatus reservationStatus;
    private String userName;
    private String facilityName;
    private String courtName;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer totalPrice;
    private LocalDateTime createAt;

    private String cancelReason;
    private LocalDateTime cancelAt;

    private PaymentStatus paymentStatus;
    private Integer refundAmount;

    // 예약 확정 및 완료 상태
    public static AdminReservationDetailResponse getReservationPayment(Reservation reservation, Payment payment) {

        return getReservationPending(reservation)
            .toBuilder()
            .paymentStatus(payment.getStatus())
            .build();
    }

    // 예약 취소 상태
    public static AdminReservationDetailResponse getReservationCancel(Reservation reservation,
        Optional<Payment> payment) {
        AdminReservationDetailResponse pending = getReservationPending(reservation);

        AdminReservationDetailResponse response = pending.toBuilder()
            .cancelReason(reservation.getCancelReason())
            .cancelAt(reservation.getCanceledAt())
            .build();
        ;

        if (payment.isPresent()) {   // payment 객체가 존재하는 경우
            Payment paymentResponse = payment.get();
            response = response.toBuilder()
                .paymentStatus(paymentResponse.getStatus())
                .refundAmount(paymentResponse.getRefundAmount())
                .build();
        }

        return response;
    }

    // 예약 대기 상태 
    public static AdminReservationDetailResponse getReservationPending(Reservation reservation) {
        return AdminReservationDetailResponse.builder()
            .reservationId(reservation.getId())
            .reservationStatus(reservation.getStatus())
            .userName(reservation.getUser().getName())
            .facilityName(reservation.getCourt().getFacility().getName())
            .courtName(reservation.getCourt().getName())
            .reservationDate(reservation.getReserveDate())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .totalPrice(reservation.getTotalPrice())
            .createAt(reservation.getCreatedAt())
            .build();
    }

}
