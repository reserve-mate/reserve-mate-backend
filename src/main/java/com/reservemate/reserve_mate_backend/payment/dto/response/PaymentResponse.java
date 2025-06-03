package com.reservemate.reserve_mate_backend.payment.dto.response;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MatchPaymentSuccessDto.class, name = "matchPaymentSuccess"),
    @JsonSubTypes.Type(value = ReservationPaymentSuccessDto.class, name = "reservePaymentSuccess"),
    @JsonSubTypes.Type(value = PaymentCancelDto.class, name = "cancelPayment"),
    @JsonSubTypes.Type(value = PaymentFailDto.class, name = "failPayment")
// 실패 dto 추가
})
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PaymentResponse {

    private String status; // success or fail

    public PaymentResponse(String status) {
        this.status = status;
    }

    // 결제 승인 성공
    public static PaymentResponse toMatchPaymentResponse(Match match) {
        PaymentResponse response = new MatchPaymentSuccessDto("success", match);
        return response;
    }

    // 예약 결제 승인 성공
    public static PaymentResponse toReservationPaymentResponse(Reservation reservation) {
        PaymentResponse response = new ReservationPaymentSuccessDto("success", reservation);
        return response;
    }

    // 결제 취소 성공
    public static PaymentResponse toPaymentCancel(String orderId, String cancelReason) {
        PaymentResponse cancelResponse = new PaymentCancelDto("cancel", orderId, cancelReason);
        return cancelResponse;
    }

    // 결제 승인 및 취소 실패 시
    public static PaymentResponse toPaymentFailed(JSONObject errObject) {
        PaymentResponse failResponse = new PaymentFailDto("fail", errObject);
        return failResponse;
    }

}
