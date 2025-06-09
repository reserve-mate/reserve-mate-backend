package com.reservemate.reserve_mate_backend.payment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentHistCntResponse {

    private Integer matchPaymentCnt;            // 매치 결제 내역 카운트
    private Integer reservationPaymentCnt;      // 예약 결제 내역 카운트

}
