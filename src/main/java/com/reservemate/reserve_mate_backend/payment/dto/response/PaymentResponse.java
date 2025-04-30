package com.reservemate.reserve_mate_backend.payment.dto.response;

import java.time.LocalDateTime;

import org.json.simple.JSONObject;

import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentMethod;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PaymentResponse {

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Integer amount;
    private String orderId;

    private String successUrl;
    private String failUrl;

    private String failReason;
    private boolean cancelYN;
    private String cancelReason;
    private LocalDateTime createdAt;

    private String errorCode;
    private String errorMsg;

    public static PaymentResponse toPaymentResponse(Payment payment, String successUrl, String failUrl) {
        PaymentResponse response = PaymentResponse.builder()
            .paymentMethod(payment.getPayMethod())
            .paymentStatus(payment.getStatus())
            .amount(payment.getAmount())
            .orderId(payment.getImpUid())
            .successUrl(successUrl)
            .failUrl(failUrl)
            .createdAt(payment.getCreatedAt())
            .build();

        return response;
    }

    public static PaymentResponse toPaymentConfirm(Payment payment) {
        return PaymentResponse.builder()
            .paymentMethod(payment.getPayMethod())
            .paymentStatus(payment.getStatus())
            .amount(payment.getAmount())
            .orderId(payment.getImpUid())
            .createdAt(payment.getCreatedAt())
            .build();
    }

    public static PaymentResponse toCancelResponse(String orderId, String cancelReason) {
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .orderId(orderId)
            .cancelReason(cancelReason)
            .paymentStatus(PaymentStatus.CANCELED)
            .build();
        return paymentResponse;
    }

    public static PaymentResponse toErrorResponse(JSONObject errorJson) {
        String code = errorJson.get("code") != null ? errorJson.get("code").toString() : "400";
        String message = errorJson.get("message") != null ? errorJson.get("message").toString() : "처리 중 에러가 발생하였습니다.";

        return PaymentResponse.builder()
            .errorCode(code)
            .errorMsg(message)
            .build();
    }

}
