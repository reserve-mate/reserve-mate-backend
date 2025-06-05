package com.reservemate.reserve_mate_backend.payment.repository.impl;

import java.sql.Time;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.facility.domain.QCourt;
import com.reservemate.reserve_mate_backend.facility.domain.QFacility;
import com.reservemate.reserve_mate_backend.match.domain.QMatch;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentStatus;
import com.reservemate.reserve_mate_backend.payment.domain.QPayment;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResDto;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResponse;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentCustomRepository;
import com.reservemate.reserve_mate_backend.reservation.domain.QReservation;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentCustomRepositoryImpl implements PaymentCustomRepository {

    private final JPAQueryFactory query;

    private QPayment payment = QPayment.payment;
    private QMatch match = QMatch.match;
    private QReservation reservation = QReservation.reservation;
    private QFacility facility = QFacility.facility;
    private QCourt court = QCourt.court;

    /* 결제 내역 */
    @Override
    public Slice<PaymentHistResponse> getPaymentHist(Long userId, String type, Pageable pageable) {

        Slice<PaymentHistResponse> payments = null;               // 최종 결제 내역

        if (type.equals("match")) {
            payments = getMatchPaymentHist(userId, pageable);
        }

        if (type.equals("reservation")) {
            payments = getReservationHist(userId, pageable);
        }

        return payments;
    }

    /* 예약 결제 내역 조회 */
    private Slice<PaymentHistResponse> getReservationHist(Long userId, Pageable pageable) {

        List<PaymentHistResponse> responses = query.select(Projections.constructor(
            PaymentHistResponse.class,
            payment.id.as("paymentId"), Expressions.constant("RESERVATION"), payment.impUid.as("orderId"),
            payment.amount.as("amount"), payment.payMethod.as("paymentMethod"), payment.status.as("paymentStatus"),
            payment.paidAt.as("paidAt"), payment.cancelReason.as("cancelReason"), payment.refundAmount.as(
                "refundAmount"), payment.canceledAt.as("cancelAt"), facility.name.as("facilityName"), court.name.as(
                    "courtName"), reservation.reserveDate.as("useDate"), reservation.startTime.as("startTime"),
            reservation.endTime.as("endTime"), reservation.id.as("reservationId"), reservation.status.as(
                "reservationStatus")
        )
        ).from(payment)
            .join(reservation).on(reservation.id.eq(payment.reservation.id))
            .join(court).on(court.id.eq(reservation.court.id))
            .join(facility).on(court.facility.id.eq(facility.id))
            .where(payment.user.id.eq(userId))
            .orderBy(payment.id.desc())
            .limit(pageable.getPageSize() + 1)
            .offset(pageable.getOffset())
            .fetch();

        return checkEndPage(pageable, responses);
    }

    /* 매치 결제 내역 조회 */
    private Slice<PaymentHistResponse> getMatchPaymentHist(Long userId, Pageable pageable) {
        Expression<Time> startTime = Expressions.timeTemplate(Time.class, "cast(MAKETIME({0}, 0, 0) as time)",
            match.matchTime).as("startTime");

        Expression<Time> endTime = Expressions.timeTemplate(Time.class, "cast(MAKETIME({0}, 0, 0) as time)",
            match.endTime).as("endTime");

        List<PaymentHistResponse> response = query.select(Projections.constructor(
            PaymentHistResponse.class,
            payment.id.as("paymentId"), Expressions.constant("MATCH"), payment.impUid.as("orderId"), payment.amount.as(
                "amount"), payment.payMethod.as("paymentMethod"), payment.status.as("paymentStatus"), payment.paidAt.as(
                    "paidAt"), payment.cancelReason.as("cancelReason"), payment.refundAmount.as("refundAmount"),
            payment.canceledAt.as("cancelAt"), facility.name.as("facilityName"), court.name.as("courtName"),
            match.matchDate.as("useDate"), startTime, endTime, match.matchId.as("matchId"), match.matchName.as(
                "matchName"), match.matchStatus.as("matchStatus")
        )
        ).from(payment)
            .join(match).on(match.matchId.eq(payment.match.matchId))
            .join(court).on(court.id.eq(match.court.id))
            .join(facility).on(court.facility.id.eq(facility.id))
            .where(payment.user.id.eq(userId))
            .orderBy(payment.id.desc())
            .limit(pageable.getPageSize() + 1)
            .offset(pageable.getOffset())
            .fetch();

        return checkEndPage(pageable, response);
    }

    @Override
    public Slice<PaymentHistResDto> getMatchPayHist(Long userId, PaymentStatus paymentStatus, Pageable pageable) {

        List<PaymentHistResDto> payHist = query.select(
            Projections.fields(PaymentHistResDto.class,
                payment.id.as("paymentId"), payment.amount.as("amount"), payment.payMethod.as("paymentMethod"),
                payment.status.as("paymentStatus"), Expressions.stringTemplate("DATE_FORMAT({0}, {1})", payment.paidAt,
                    ConstantImpl.create("%Y-%m-%d %H:%i")).as("paidAt"), payment.cancelReason.as("cancelReason"),
                payment.refundAmount.as("refundAmount"), Expressions.stringTemplate("DATE_FORMAT({0}, {1})",
                    payment.canceledAt, ConstantImpl.create("%Y-%m-%d %H:%i")).as("canceledAt"), payment.match.matchName
                        .as("matchName")
            )
        )
            .from(payment)
            .where(
                payment.user.id.eq(userId), paymentStatusEq(paymentStatus)
            )
            .limit(pageable.getPageSize() + 1)
            .fetch();

        return checkEndPage(pageable, payHist);
    }

    /* 결제 상태 where */
    private BooleanExpression paymentStatusEq(PaymentStatus paymentStatus) {
        return (paymentStatus != null) ? payment.eq(payment) : null;
    }

    private <T> Slice<T> checkEndPage(Pageable pageable, List<T> payHist) {
        boolean hasNext = false;

        if (payHist.size() > pageable.getPageSize()) {
            hasNext = true;
            payHist.remove(pageable.getPageSize()); // 한개 더 가져왔으니 더 가져온 데이터 삭제
        }

        return new SliceImpl<>(payHist, pageable, hasNext);
    }

}
