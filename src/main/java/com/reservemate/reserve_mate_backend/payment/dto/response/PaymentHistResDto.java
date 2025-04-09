package com.reservemate.reserve_mate_backend.payment.dto.response;

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
public class PaymentHistResDto {

    private Long paymentId;
    private Integer amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private String paidAt;

    private String cancelReason;
    private Integer refundAmount;
    private String canceledAt;

    private String matchName;

}
