package com.reservemate.reserve_mate_backend.payment.service;

import java.net.http.HttpResponse;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.payment.client.PayClient;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentResponse;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
import com.reservemate.reserve_mate_backend.payment.util.PaymentUtil;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.dto.request.ConfirmReservationRequest;
import com.reservemate.reserve_mate_backend.reservation.dto.request.ReservationPaymentRequest;
import com.reservemate.reserve_mate_backend.reservation.validator.Validator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentReservationService {

    private final PaymentRepository paymentRepository;
    private final Validator validator;
    private final PayClient payClient;
    private final ApplicationEventPublisher eventPublisher;

    /* 예약 검증 후 결제 */
    @Transactional
    public PaymentResponse reservationPayment(ReservationPaymentRequest paymentRequest) {

        /* 예약 확정 시 데이터 검증 */
        Reservation reservation = validator.reservationConfirmValid(paymentRequest.getReservationId());

        PaymentResponse response = null;

        try {
            HttpResponse httpResponse = payClient.requestPay(paymentRequest.getOrderId(), paymentRequest
                .getPaymentKey(), paymentRequest.getAmount());
            if (httpResponse.statusCode() != 200) {  // 결제 승인 시 에러로 인한 취소는 DB에 넣지 않음
                String failMsg = PaymentUtil.getFailReason(httpResponse.body().toString());
                payClient.requestCancelPay(paymentRequest.getPaymentKey(), failMsg, paymentRequest.getAmount());

                response = PaymentResponse.toPaymentCancel(paymentRequest.getOrderId(), failMsg);
            } else {
                Payment payment = paymentRequest.toEntity(reservation);
                paymentRepository.save(payment);
                eventPublisher.publishEvent(new ConfirmReservationRequest(reservation));
                response = PaymentResponse.toReservationPaymentResponse(reservation);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.PAYMETN_ERROR);
        }

        return response;

    }

}
