package com.reservemate.reserve_mate_backend.payment.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Base64;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;

public class PaymentUtil {

    // 결제 취소(토스)
    public static HttpResponse requestCancelPay(String tossSecret, String tossApiUrl,
        String apiName, String cancelReason, int cancelAmount) throws IOException, InterruptedException {
        String jsonBody = String.format("{\"cancelReason\":\"%s\", \"cancelAmount\":%d}", cancelReason, cancelAmount);
        if (cancelAmount == 0)
            jsonBody = String.format("{\"cancelReason\":\"%s\"}", cancelReason);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(tossApiUrl + apiName))
            .header("Authorization", "Basic " + PaymentUtil.getPayAuth(tossSecret))
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        HttpResponse httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return httpResponse;
    }

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
