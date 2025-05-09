package com.reservemate.reserve_mate_backend.payment.dto.request;

import java.util.UUID;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.dto.request.RequestMatchDto;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentMethod;
import com.reservemate.reserve_mate_backend.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RequestPaymentDto {

    private String orderId;
    private Integer amount;    // 가격
    private Match match;
    private User user;

    public static RequestPaymentDto toRequestPaymentDto(RequestMatchDto requestMatchDto, User user, Match match) {
        RequestPaymentDto requestPaymentDto = new RequestPaymentDto(UUID.randomUUID().toString(), requestMatchDto
            .getAmount(), match, user);

        return requestPaymentDto;
    }

    public Payment toEntity() {
        return Payment.builder()
            .impUid(this.orderId)
            .amount(this.amount)
            .user(this.user)
            .match(this.match)
            .build();
    }

}
