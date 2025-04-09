package com.reservemate.reserve_mate_backend.payment.repository.impl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.ConstantImpl;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentStatus;
import com.reservemate.reserve_mate_backend.payment.domain.QPayment;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResDto;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentCustomRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PaymentCustomRepositoryImpl implements PaymentCustomRepository {

    private final JPAQueryFactory query;

    private QPayment payment = QPayment.payment;

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

    private Slice<PaymentHistResDto> checkEndPage(Pageable pageable, List<PaymentHistResDto> payHist) {
        boolean hasNext = false;

        if (payHist.size() > pageable.getPageSize()) {
            hasNext = true;
            payHist.remove(pageable.getPageSize()); // 한개 더 가져왔으니 더 가져온 데이터 삭제
        }

        return new SliceImpl<>(payHist, pageable, hasNext);
    }

}
