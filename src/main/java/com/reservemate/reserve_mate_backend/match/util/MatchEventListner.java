package com.reservemate.reserve_mate_backend.match.util;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.payment.dto.request.CancelPaymentDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.MatchCancelPaymentRequest;
import com.reservemate.reserve_mate_backend.payment.dto.request.RequestPaymentDto;
import com.reservemate.reserve_mate_backend.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/*
 * 이벤트를 Return값을 받을 경우 다른 @EventListener 자동 발행
 * 즉, 반환된 Return 타입과 같은 @EventListener를 실행해서 연쇄작용을 하게 함
 */
@Component
@RequiredArgsConstructor
@Log4j2
public class MatchEventListner {

    private final PaymentService paymentService;

    /* 매치 삭제 후 취소 */
    @EventListener
    public void deleteMatchCancel(MatchCancelPaymentRequest request) {
        log.info("매치 삭제 후 환불");
        paymentService.matchCancelPayment(request.getPlayers());
    }

    /* 매치 검증 후 결제 요청 */
    @EventListener
    public void requestPayment(RequestPaymentDto requestPaymentDto) {
        log.info("매치 검증 후 결제 요청");
        paymentService.requestPayment(requestPaymentDto);
    }

    @EventListener
    public void cancelPayment(CancelPaymentDto cancelPaymentDto) {
        log.info("매치 취소 검증 후 결제 취소");
        try {
            paymentService.cancelPayment(cancelPaymentDto);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("결제 처리 중 에러가 발생하였습니다.");
        }
    }

}
