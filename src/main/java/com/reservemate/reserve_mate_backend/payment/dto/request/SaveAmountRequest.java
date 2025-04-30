package com.reservemate.reserve_mate_backend.payment.dto.request;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
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
public class SaveAmountRequest {

    private Integer amount;
    private String orderId;
    private String paymentKey;
    private Long matchId;

    public Payment toEntity(Match match, User user) {
        Payment payment = Payment.builder()
            .impUid(this.orderId)
            .merchantUid(this.paymentKey)
            .amount(this.amount)
            .payMethod(PaymentMethod.CARD)
            .match(match)
            .user(user)
            .build();

        return payment;
    }

    public String getFailReason(String responseBody) {
        String failMsg = "결제처리에 실패하였습니다.";

        if (!responseBody.equals("")) {
            JSONParser jsonParser = new JSONParser();
            JSONObject object = null;
            try {
                object = (JSONObject) jsonParser.parse(responseBody);
                failMsg = object.get("message").toString();
            } catch (ParseException e) {
                e.printStackTrace();
                throw new ApiException(ErrorCode.SERVER_ERROR);
            }
        }

        return failMsg;
    }

}
