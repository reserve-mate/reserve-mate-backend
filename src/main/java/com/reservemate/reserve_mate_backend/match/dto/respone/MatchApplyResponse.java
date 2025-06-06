package com.reservemate.reserve_mate_backend.match.dto.respone;

import com.reservemate.reserve_mate_backend.match.dto.request.RequestMatchDto;
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
public class MatchApplyResponse {

    private Integer amount;         // 매치 가격
    private String orderId;         // 주문 번호
    private String orderName;       // 매치 명
    private String customerName;    // 고객 명
    private String customerEmail;   // 고객 이메일
    private String successUrl;      // 성공 Url
    private String failUrl;         // 실패 Url

    public static MatchApplyResponse toMatchApplyResponse(RequestMatchDto requestMatchDto, User user, String matchName,
        String successUrl, String failUrl) {
        return MatchApplyResponse.builder()
            .orderName(matchName)
            .customerName(user.getName())
            .customerEmail(user.getEmail())
            .amount(requestMatchDto.getAmount())
            .orderId(requestMatchDto.getOrderId())
            .successUrl(successUrl)
            .failUrl(failUrl)
            .build();
    }

}
