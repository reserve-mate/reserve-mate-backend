package com.reservemate.reserve_mate_backend.payment.dto.response;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.querydsl.core.annotations.QueryProjection;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentMethod;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PaymentHistResponse {

    private Long paymentId;
    private String paymentType; // MATCH or RESERVATION
    private String orderId;
    private Integer amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private LocalDateTime paidAt;

    private String cancelReason;
    private Integer refundAmount;
    private LocalDateTime cancelAt;

    private String facilityName;
    private String courtName;
    private LocalDate useDate;
    private LocalTime startTime;
    private LocalTime endTime;

    private Long refId;
    private String refName;

    // native용 생성자
    public PaymentHistResponse(
        Long paymentId,
        String paymentType,
        String orderId,
        Integer amount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        LocalDateTime paidAt,
        String cancelReason,
        Integer refundAmount,
        LocalDateTime cancelAt,
        String facilityName,
        String courtName,
        LocalDate useDate,
        LocalTime startTime,
        LocalTime endTime,
        Long refId,
        String refName
    ) {
        this.paymentId = paymentId;
        this.paymentType = paymentType;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paidAt = paidAt;
        this.cancelReason = cancelReason;
        this.refundAmount = refundAmount;
        this.cancelAt = cancelAt;
        this.facilityName = facilityName;
        this.courtName = courtName;
        this.useDate = useDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.refId = refId;
        this.refName = refName;
    }

    // 매치 결제용 생성자
    @QueryProjection
    public PaymentHistResponse(
        Long paymentId,
        String paymentType,
        String orderId,
        Integer amount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        LocalDateTime paidAt,
        String cancelReason,
        Integer refundAmount,
        LocalDateTime cancelAt,
        String facilityName,
        String courtName,
        LocalDate useDate,
        Time startTime,
        Time endTime,
        Long matchId,
        String matchName
    ) {
        this.paymentId = paymentId;
        this.paymentType = paymentType;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paidAt = paidAt;
        this.cancelReason = cancelReason;
        this.refundAmount = refundAmount;
        this.cancelAt = cancelAt;
        this.facilityName = facilityName;
        this.courtName = courtName;
        this.useDate = useDate;
        this.startTime = startTime.toLocalTime();
        this.endTime = endTime.toLocalTime();
        this.refId = matchId;
        this.refName = matchName;
    }

    // 예약 결제용 생성자
    @QueryProjection
    public PaymentHistResponse(
        Long paymentId,
        String paymentType,
        String orderId,
        Integer amount,
        PaymentMethod paymentMethod,
        PaymentStatus paymentStatus,
        LocalDateTime paidAt,
        String cancelReason,
        Integer refundAmount,
        LocalDateTime cancelAt,
        String facilityName,
        String courtName,
        LocalDate useDate,
        LocalTime startTime,
        LocalTime endTime,
        Long reservationId
    ) {
        this.paymentId = paymentId;
        this.paymentType = paymentType;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.paidAt = paidAt;
        this.cancelReason = cancelReason;
        this.refundAmount = refundAmount;
        this.cancelAt = cancelAt;
        this.facilityName = facilityName;
        this.courtName = courtName;
        this.useDate = useDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.refId = reservationId;
    }

}
