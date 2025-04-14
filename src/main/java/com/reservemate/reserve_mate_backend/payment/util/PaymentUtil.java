package com.reservemate.reserve_mate_backend.payment.util;

import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;

public class PaymentUtil {

    // base64 암호화
    public static String getPayAuth(String tossSecret) {
        return Base64.getEncoder().encodeToString(tossSecret.getBytes());
    }

    // 승인 요청에 사용할 JSON 객체를 만듦
    public static String requestBody(SaveAmountRequest amountRequest) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode requestObj = mapper.createObjectNode()
            .put("orderId", amountRequest.getOrderId())
            .put("amount", amountRequest.getAmount())
            .put("paymentKey", amountRequest.getPaymentKey());

        String requestBody = "";
        try {
            requestBody = mapper.writeValueAsString(requestObj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }

        return requestBody;
    }

}
