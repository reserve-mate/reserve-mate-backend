package com.reservemate.reserve_mate_backend.match.dto.request;

import com.reservemate.reserve_mate_backend.payment.domain.PaymentMethod;

import jakarta.validation.constraints.NotNull;
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
public class RequestMatchDto {

    private String orderId; // 주문 번호

    @NotNull
    private Long userId;

    @NotNull
    private Long matchId;
    private PaymentMethod paymentMethod;

    @NotNull
    private Integer amount; // 매치 가격

}
