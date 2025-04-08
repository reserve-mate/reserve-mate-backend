package com.reservemate.reserve_mate_backend.payment.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.payment.dto.request.CancelPayRequestDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.ConfirmRequestDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.PaymentHistReqDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResDto;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentResponse;
import com.reservemate.reserve_mate_backend.payment.service.PaymentService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/payHist")
    public ResponseEntity<Slice<PaymentHistResDto>> getPayHist(@RequestBody PaymentHistReqDto histReqDto) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(histReqDto));
    }

    @PutMapping("/cancel")
    public ResponseEntity<PaymentResponse> requestCancelPayment(@RequestBody CancelPayRequestDto cancelPayRequestDto) {
        return ResponseEntity.ok(paymentService.requestCancelPayment(cancelPayRequestDto));
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> requestPayment(@RequestBody ConfirmRequestDto confirmRequestDto) {
        return ResponseEntity.ok(paymentService.requestPayment(confirmRequestDto));
    }

    /* 토스에 결제 승인받기 */
    @PostMapping("/success")
    public ResponseEntity<PaymentResponse> postMethodName(@RequestBody SaveAmountRequest amountRequest) {
        return ResponseEntity.ok(paymentService.requestPayConfirm(amountRequest));
    }

    /*
     * 결제의 금액을 세션에 임시저장
     * 결제 과정에서의 악의적으로 결제 금액이 바뀌는 것을 확인하는 용도
     */
    @PostMapping("/saveAmount")
    public ResponseEntity<String> saveAmount(HttpSession session, @RequestBody SaveAmountRequest amountRequest) {
        session.setAttribute(amountRequest.getOrderId(), amountRequest.getAmount());
        return ResponseEntity.ok("Payment save successful");
    }

}
