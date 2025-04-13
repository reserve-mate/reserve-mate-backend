package com.reservemate.reserve_mate_backend.match.util;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.payment.dto.request.RequestPaymentDto;
import com.reservemate.reserve_mate_backend.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@RequiredArgsConstructor
@Log4j2
public class MatchEventListner {

    private final PaymentService paymentService;

    /* 매치 검증 후 결제 요청 */
    @EventListener
    public void requestPayment(RequestPaymentDto requestPaymentDto) {
        log.info("매치 검증 후 결제 요청");
        paymentService.requestPayment(requestPaymentDto);
    }

}
