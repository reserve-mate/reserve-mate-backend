package com.reservemate.reserve_mate_backend.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CancelPaymentDto {

    private String orderId;
    private String cancelReason;

}
