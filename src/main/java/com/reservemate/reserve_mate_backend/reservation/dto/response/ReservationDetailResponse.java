package com.reservemate.reserve_mate_backend.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;
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
@Builder
@Getter
@Setter
public class ReservationDetailResponse {

    private Long reservationId;
    private ReservationStatus reservationStatus;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String bookedName;
    private String reservationNumber;

    private String userEmail;
    private String userPhone;

    private Long facilityId;
    private String facilityName;
    private String courtName;
    private SportType sportType;
    private String address;

    private Integer totalPrice;
    private PaymentStatus paymentStatus;
    private String paymentMethod;
    private Integer refundPayment;
    private String cancelReason;
    private LocalDateTime canceledAt;

    // 예약 상세 데이터 가져오기
    public static ReservationDetailResponse toReservationDetailPending(Reservation reservation) {

        ReservationDetailResponse response = ReservationDetailResponse.builder()
            .reservationId(reservation.getId())
            .reservationStatus(reservation.getStatus())
            .reservationDate(reservation.getReserveDate())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .bookedName(reservation.getUser().getName())
            .reservationNumber(reservation.getReservationNumber())
            .userEmail(reservation.getUser().getEmail())
            .userPhone(reservation.getUser().getPhone())
            .facilityId(reservation.getCourt().getFacilityId())
            .sportType(reservation.getCourt().getFacility().getSportType())
            .facilityName(reservation.getCourt().getFacility().getName())
            .courtName(reservation.getCourt().getName())
            .address(reservation.getCourt().getFacility().getAddress().getFullAddress())
            .totalPrice(reservation.getTotalPrice())
            .build();

        return response;
    }

    // 예약 상세 데이터 가져오기
    public static ReservationDetailResponse toReservationDetailResponse(Reservation reservation, Payment payment) {

        ReservationDetailResponse response = ReservationDetailResponse.builder()
            .reservationId(reservation.getId())
            .reservationStatus(reservation.getStatus())
            .reservationDate(reservation.getReserveDate())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .bookedName(reservation.getUser().getName())
            .reservationNumber(reservation.getReservationNumber())
            .facilityId(reservation.getCourt().getFacilityId())
            .sportType(reservation.getCourt().getFacility().getSportType())
            .facilityName(reservation.getCourt().getFacility().getName())
            .courtName(reservation.getCourt().getName())
            .address(reservation.getCourt().getFacility().getAddress().getFullAddress())
            .totalPrice(reservation.getTotalPrice())
            .cancelReason(reservation.getCancelReason())
            .canceledAt(reservation.getCanceledAt())
            .paymentMethod(payment.getPayMethod())
            .paymentStatus(payment.getStatus())
            .build();

        return response;
    }

    // 예약 취소 상세 데이터 가져오기
    public static ReservationDetailResponse toReservationCancelResponse(Reservation reservation,
        Optional<Payment> payment) {
        ReservationDetailResponse response = ReservationDetailResponse.builder()
            .reservationId(reservation.getId())
            .reservationStatus(reservation.getStatus())
            .reservationDate(reservation.getReserveDate())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .bookedName(reservation.getUser().getName())
            .reservationNumber(reservation.getReservationNumber())
            .facilityId(reservation.getCourt().getFacilityId())
            .sportType(reservation.getCourt().getFacility().getSportType())
            .facilityName(reservation.getCourt().getFacility().getName())
            .courtName(reservation.getCourt().getName())
            .address(reservation.getCourt().getFacility().getAddress().getFullAddress())
            .totalPrice(reservation.getTotalPrice())
            .cancelReason(reservation.getCancelReason())
            .canceledAt(reservation.getCanceledAt())
            .build();

        if (payment.isPresent()) {
            Payment payResponse = payment.get();
            response.setRefundPayment(payResponse.getRefundAmount());
        }

        return response;
    }

}
