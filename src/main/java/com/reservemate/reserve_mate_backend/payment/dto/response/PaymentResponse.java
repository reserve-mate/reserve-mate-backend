package com.reservemate.reserve_mate_backend.payment.dto.response;

import com.reservemate.reserve_mate_backend.payment.domain.PaymentMethod;

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
    private Integer amount;
    private String orderId;
    private String customerName;

    private String successUrl;
    private String failUrl;

    private String failReason;
    private boolean cancelYN;
    private String cancelReason;
    private String createdAt;

}
