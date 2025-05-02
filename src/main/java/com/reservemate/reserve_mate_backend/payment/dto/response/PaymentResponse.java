package com.reservemate.reserve_mate_backend.payment.dto.response;

import java.time.LocalDateTime;

import org.json.simple.JSONObject;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentMethod;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = MatchPaymentSuccessDto.class, name = "matchPaymentSuccess")
// 실패 dto 추가
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class PaymentResponse {

    private String status; // success or fail

    private PaymentMethod paymentMethod;
    private PaymentStatus paymentStatus;
    private Integer amount;
    private String orderId;

    private String failReason;
    private String cancelReason;
    private LocalDateTime createdAt;

    private String errorCode;
    private String errorMsg;

    public PaymentResponse(String status) {
        this.status = status;
    }

    // 결제 승인 성공
    public static PaymentResponse toMatchPaymentResponse(Match match) {
        PaymentResponse response = new MatchPaymentSuccessDto("success", match);
        return response;
    }

    // 결제 취소 성공
    public static PaymentResponse toPaymentCancel(String orderId, String cancelReason) {
        PaymentResponse cancelResponse = new PaymentCancelDto("cancel", orderId, cancelReason);
        return cancelResponse;
    }

    // 결제 승인 및 취소 실패 시
    public static PaymentResponse toPaymentFailed(JSONObject errObject) {
        PaymentResponse failResponse = new PaymentFailDto("failed", errObject);
        return failResponse;
    }

    public static PaymentResponse toPaymentResponse(Payment payment) {
        PaymentResponse response = PaymentResponse.builder()
            .paymentMethod(payment.getPayMethod())
            .paymentStatus(payment.getStatus())
            .amount(payment.getAmount())
            .orderId(payment.getImpUid())
            .createdAt(payment.getCreatedAt())
            .build();

        return response;
    }

    public static PaymentResponse toPaymentConfirm(Payment payment) {
        return PaymentResponse.builder()
            .paymentMethod(payment.getPayMethod())
            .paymentStatus(payment.getStatus())
            .amount(payment.getAmount())
            .orderId(payment.getImpUid())
            .createdAt(payment.getCreatedAt())
            .build();
    }

    public static PaymentResponse toCancelResponse(String orderId, String cancelReason) {
        PaymentResponse paymentResponse = PaymentResponse.builder()
            .orderId(orderId)
            .cancelReason(cancelReason)
            .paymentStatus(PaymentStatus.CANCELED)
            .build();
        return paymentResponse;
    }

    public static PaymentResponse toErrorResponse(JSONObject errorJson) {
        String code = errorJson.get("code") != null ? errorJson.get("code").toString() : "400";
        String message = errorJson.get("message") != null ? errorJson.get("message").toString() : "처리 중 에러가 발생하였습니다.";

        return PaymentResponse.builder()
            .errorCode(code)
            .errorMsg(message)
            .build();
    }

}
