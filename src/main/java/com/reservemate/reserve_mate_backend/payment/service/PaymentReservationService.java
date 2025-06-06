package com.reservemate.reserve_mate_backend.payment.service;

import java.net.http.HttpResponse;

import org.json.simple.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.util.Utils;
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

    /* 예약 결제 취소 상태 체크 */
    public PaymentResponse reservationPaymentCancelChk(String reservationNumber) {
        if (reservationNumber == null || reservationNumber.isBlank()) {
            throw new ApiException(ErrorCode.MISSING_QUERY_PARAM);
        }

        Reservation reservation = validator.reservationCancelChk(reservationNumber);
        Payment payment = paymentRepository.findByReservation(reservation).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_PAYMENT));
        payment.isCancel();

        return PaymentResponse.toPaymentCancel(payment.getImpUid(), reservation.getCancelReason());
    }

    /* 예약 취소 시 결제 취소 */
    @Transactional
    public void reservationCancelPayment(Long reservationId, String cancelReason) {

        Payment payment = paymentRepository.findByReservationId(reservationId).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_PAYMENT));
        payment.isPaid();

        try {
            int refundAmount = payment.reservationRefundAmount();
            HttpResponse response = payClient.requestCancelPay(payment.getMerchantUid(), cancelReason, refundAmount);
            if (response.statusCode() == 200) {  // 결제 취소를 성공한 경우
                payment.cancel(cancelReason, refundAmount);
            } else { // 결제 취소를 실패한 경우
                JSONObject errorResponse = Utils.stringToJson(response.body().toString());
                String message = errorResponse.get("message") != null ? errorResponse.get("message").toString()
                    : "처리 중 에러가 발생하였습니다.";

                throw new IllegalArgumentException(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }
    }

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
                String paymethod = PaymentUtil.getPaymentMethod(httpResponse.body().toString());
                Payment payment = paymentRequest.toEntity(reservation, paymethod);
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
