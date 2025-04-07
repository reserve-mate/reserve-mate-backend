package com.reservemate.reserve_mate_backend.payment.dto.request;

import java.util.UUID;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentMethod;
import com.reservemate.reserve_mate_backend.user.domain.User;

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

    private PaymentMethod paymentMethod;    // 결제 타입 (현금/카드)
    private String orderId;
    private Integer amount;    // 가격
    private Long userId;    // 회원 아이디
    private Long matchId;   // 신청한 매치
    private String successUrl;  // 성공한 경우 redirect URL
    private String failUrl;     // 실패한 경우 redirect URL

    public Payment toEntity(ConfirmRequestDto confirmRequestDto, Match match, User user) {
        return Payment.builder()
            .impUid(UUID.randomUUID().toString())
            .amount(confirmRequestDto.getAmount())
            .payMethod(confirmRequestDto.getPaymentMethod())
            .user(user)
            .match(match)
            .build();
    }

}
