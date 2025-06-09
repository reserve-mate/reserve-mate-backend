package com.reservemate.reserve_mate_backend.payment.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;
import com.reservemate.reserve_mate_backend.payment.dto.request.CancelPayRequestDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.PaymentHistReqDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistCntResponse;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResDto;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResponse;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentResponse;
import com.reservemate.reserve_mate_backend.payment.service.PaymentReservationService;
import com.reservemate.reserve_mate_backend.payment.service.PaymentService;
import com.reservemate.reserve_mate_backend.reservation.dto.request.ReservationPaymentRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentReservationService paymentReservationService;

    @GetMapping("/reservationCancelChk")
    public ResponseEntity<PaymentResponse> reservationCancelChk(
        @RequestParam("reservationNumber") String reservationNumber) {
        return ResponseEntity.ok(paymentReservationService.reservationPaymentCancelChk(reservationNumber));
    }

    /* 예약 결제 */
    @PostMapping("/reservationApprove")
    public ResponseEntity<PaymentResponse> reservationPayment(@RequestBody ReservationPaymentRequest paymentRequest) {
        return ResponseEntity.ok(paymentReservationService.reservationPayment(paymentRequest));
    }

    @GetMapping("/cancelStatus")
    public ResponseEntity<PaymentResponse> checkCancelStatus(@RequestParam("orderId") String orderId) {
        return ResponseEntity.ok(paymentService.checkCancelStatus(orderId));
    }

    @GetMapping("/getPaymentHistCnt")
    public ResponseEntity<PaymentHistCntResponse> getPaymentHistCnt(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(paymentService.getPaymentHistCnt(customUserDetails.getId()));
    }

    /* 결제 내역 조회 */
    @GetMapping("/paymentHist")
    public ResponseEntity<Slice<PaymentHistResponse>> getpaymentHist(
        @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam("type") String type,
        @RequestParam("pageNum") Integer pageNum) {
        return ResponseEntity.ok(paymentService.getpaymentHist(customUserDetails.getId(), type, pageNum));
    }

    /* 결제 이력 조회 */
    @GetMapping("/payHist")
    public ResponseEntity<Slice<PaymentHistResDto>> getPayHist(@RequestBody PaymentHistReqDto histReqDto) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(histReqDto));
    }

    /* 결제 취소 */
    @PutMapping("/cancel")
    public ResponseEntity<PaymentResponse> requestCancelPayment(@RequestBody CancelPayRequestDto cancelPayRequestDto) {
        return ResponseEntity.ok(paymentService.requestCancelPayment(cancelPayRequestDto));
    }

    /* 토스에 결제 승인받기 */
    @PostMapping("/approve")
    public ResponseEntity<PaymentResponse> postMethodName(HttpServletRequest request,
        @RequestBody SaveAmountRequest amountRequest) {
        return ResponseEntity.ok(paymentService.requestPayConfirm(request, amountRequest));
    }

}
