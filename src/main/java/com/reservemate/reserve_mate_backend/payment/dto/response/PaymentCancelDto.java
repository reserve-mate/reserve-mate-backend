package com.reservemate.reserve_mate_backend.payment.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentCancelDto extends PaymentResponse {

    private String orderId;
    private String cancelReason;
    private LocalDateTime cancelAt;

    public PaymentCancelDto(String status, String orderId, String cancelReason) {
        super(status);
        this.orderId = orderId;
        this.cancelReason = cancelReason;
    }

}
