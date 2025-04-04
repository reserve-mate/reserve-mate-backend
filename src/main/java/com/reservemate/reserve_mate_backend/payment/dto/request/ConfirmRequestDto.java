package com.reservemate.reserve_mate_backend.payment.dto.request;

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
public class ConfirmRequestDto {

    private String orderId;
    private Long amount;
    private String paymentKey;

}
