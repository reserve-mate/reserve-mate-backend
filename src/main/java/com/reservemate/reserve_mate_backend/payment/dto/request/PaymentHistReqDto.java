package com.reservemate.reserve_mate_backend.payment.dto.request;

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
public class PaymentHistReqDto {

    private Long userId;
    private PaymentStatus paymentStatus;
    private String payType; // 매치 결제 내역 or 구장 예약 결제 내역 (match, reserve)

}
