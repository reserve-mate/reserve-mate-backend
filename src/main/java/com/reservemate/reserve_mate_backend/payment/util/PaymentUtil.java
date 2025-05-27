package com.reservemate.reserve_mate_backend.payment.util;

import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    public static String requestBody(String impUid, String paymentKey, Integer amount) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode requestObj = mapper.createObjectNode()
            .put("orderId", impUid)
            .put("amount", amount)
            .put("paymentKey", paymentKey);

        String requestBody = "";
        try {
            requestBody = mapper.writeValueAsString(requestObj);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }

        return requestBody;

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

    public static String getFailReason(String responseBody) {
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
