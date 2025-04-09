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

    public static PaymentResponse toErrorResponse(JSONObject errorJson) {
        PaymentResponse response = PaymentResponse.builder()
            .errorCode(errorJson.get("code").toString())
            .errorMsg(errorJson.get("message").toString())
            .build();
        return response;
    }

}
