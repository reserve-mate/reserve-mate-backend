package com.reservemate.reserve_mate_backend.payment.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.payment.util.ReturnPolicy;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.user.domain.User;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payments")
@DynamicUpdate
@SQLDelete(sql = "UPDATE payments SET deleted = true WHERE payment_id = ?")
@SQLRestriction("deleted = false")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", updatable = false)
    private Long id;

    @Column(name = "imp_uid", nullable = false)
    private String impUid;  // orderId

    @Column(name = "merchant_uid")
    private String merchantUid; // paymentId

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "pay_method", nullable = false)
    private PaymentMethod payMethod;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "cancel_reason")
    private String cancelReason;

    private Integer refundAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    private Match match;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Builder
    public Payment(
        String impUid,
        String merchantUid,
        Integer amount,
        User user,
        PaymentMethod payMethod,
        Match match) {
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.payMethod = payMethod;
        this.user = user;
        this.match = match;
    }

    // @Builder 여러개 있는 경우 Lombok이 한개만 적용(한개만 적용 추천)
    public Payment(
        String impUid,
        String merchantUid,
        Integer amount,
        User user,
        PaymentMethod payMethod,
        Reservation reservation) {
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.payMethod = payMethod;
        this.user = user;
        this.reservation = reservation;
    }

    @Builder
    public Payment(
        Long id,
        String impUid,
        String merchantUid,
        Integer amount,
        User user,
        PaymentMethod payMethod,
        Match match) {
        this.id = id;
        this.impUid = impUid;
        this.merchantUid = merchantUid;
        this.amount = amount;
        this.status = PaymentStatus.READY;
        this.payMethod = payMethod;
        this.user = user;
        this.match = match;
    }

    // 예약 취소시각에 따른 환불
    public int reservationRefundAmount() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        LocalDateTime reserveDateTime = LocalDateTime.of(this.reservation.getReserveDate(), this.reservation
            .getStartTime());

        Duration duration = Duration.between(nowDateTime, reserveDateTime);
        long hourUntilStart = duration.toHours();

        int refund = 0;
        if (hourUntilStart >= ReturnPolicy.TWO_DAY_AGO_BY_HOUR) {    // 48시간 전
            refund = this.amount;
        } else if (hourUntilStart >= ReturnPolicy.ONE_DAY_AGO_BY_HOUR) {  // 24~48시간 전
            refund = (int) (this.amount * ReturnPolicy.RETURN_50);
        } else if (hourUntilStart > 0) {
            refund = 0;
        }

        return refund;
    }

    // 취소 시각에 따른 환불
    public int refundAmount() {
        LocalDateTime toDateTIme = LocalDateTime.now();
        LocalDateTime matchDateTime = LocalDateTime.of(this.match.getMatchDate(), LocalTime.of(this.match
            .getMatchTime(), 0, 0));

        int diffHour = (int) ChronoUnit.HOURS.between(toDateTIme, matchDateTime);
        int diffMinute = (int) ChronoUnit.MINUTES.between(toDateTIme, matchDateTime);

        int matchPrice = this.match.getMatchPrice();

        if (diffHour <= ReturnPolicy.TWO_DAY_AGO_BY_HOUR && diffHour >= ReturnPolicy.ONE_DAY_AGO_BY_HOUR) {
            // 매치 시작 시간 24시간 이내 취소 시 80% 환불
            matchPrice = (int) (matchPrice * ReturnPolicy.RETURN_80);
        } else if (diffMinute < ReturnPolicy.ONE_DAY_AGO_BY_MINUTE && diffMinute >= ReturnPolicy.ONE_HOUR_HALF_AGO) {
            // 매치 당일 ~ 90분 전까지는 20% 환불
            matchPrice = (int) (matchPrice * ReturnPolicy.RETURN_20);
        } else if (diffMinute < ReturnPolicy.ONE_HOUR_HALF_AGO) {
            matchPrice = 0;
        }

        return matchPrice;
    }

    public void isCancel() {
        if (this.status != PaymentStatus.CANCELED) {
            throw new ApiException(ErrorCode.NOT_CANCEL_PAYMENT);
        }
    }

    // 가격 검증
    public void verifyPayment(int amount) {
        if (this.amount != amount) {
            throw new ApiException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
        }
    }

    // 결제 요청된 데이터 인지
    public void isNotReady() {
        if (this.status != PaymentStatus.READY) {
            throw new ApiException(ErrorCode.NOT_READY_PAYEMNT);
        }
    }

    // 결제된 데이터인지
    public void isPaid() {
        if (this.status != PaymentStatus.PAID) {
            throw new ApiException(ErrorCode.NOT_PAID);
        }
    }

    // 결제
    public void markAsPaid(String paymentKey) {
        this.status = PaymentStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.merchantUid = paymentKey;
    }

    public void markAsFailed() {
        this.status = PaymentStatus.FAILED;
    }

    public void cancel(String reason, int cancelAmount) {
        this.status = PaymentStatus.CANCELED;
        this.cancelReason = reason;
        this.canceledAt = LocalDateTime.now();
        this.refundAmount = cancelAmount;
    }

    public void refund(String reason) {
        this.status = PaymentStatus.REFUNDED;
        this.cancelReason = reason;
        this.canceledAt = LocalDateTime.now();
    }

    // 가격 총 합 구하기
    public static Integer getTotalRevenues(List<Payment> payments) {
        return payments.stream()
            .mapToInt((payment) -> payment.getAmount() - ((payment.getRefundAmount() == null) ? 0 : payment
                .getRefundAmount()))
            .sum();
    }

}
