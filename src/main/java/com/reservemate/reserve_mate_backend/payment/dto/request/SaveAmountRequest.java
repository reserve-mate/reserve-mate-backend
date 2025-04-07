package com.reservemate.reserve_mate_backend.payment.dto.request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.payment.util.PaymentUtil;

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

    public String getFailReason(String responseBody) {
        String failMsg = "결제처리에 실패하였습니다.";

        JSONParser jsonParser = new JSONParser();
        JSONObject object = null;
        try {
            object = (JSONObject) jsonParser.parse(responseBody);
            failMsg = object.get("message").toString();
        } catch (ParseException e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }

        return failMsg;
    }

    public HttpResponse requestPay(String tossSecret, String tossApiUrl,
        String apiName) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(tossApiUrl + apiName))
            .header("Authorization", "Basic " + PaymentUtil.getPayAuth(tossSecret))
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString(PaymentUtil.requestBody(this)))
            .build();

        HttpResponse httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return httpResponse;
    }

}
