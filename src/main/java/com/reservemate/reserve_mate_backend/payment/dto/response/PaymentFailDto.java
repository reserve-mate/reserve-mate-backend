package com.reservemate.reserve_mate_backend.payment.dto.response;

import org.json.simple.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PaymentFailDto extends PaymentResponse {

    private String errorCode;
    private String errorMsg;

    public PaymentFailDto(String status, JSONObject errorJson) {
        super(status);
        String code = errorJson.get("code") != null ? errorJson.get("code").toString() : "400";
        String message = errorJson.get("message") != null ? errorJson.get("message").toString() : "처리 중 에러가 발생하였습니다.";

        this.errorCode = code;
        this.errorMsg = message;
    }

}
