package com.reservemate.reserve_mate_backend.payment.client.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.payment.client.PayClient;
import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;
import com.reservemate.reserve_mate_backend.payment.util.PaymentUtil;

@Component
public class PayClientImpl implements PayClient {

    @Override
    public HttpResponse requestPay(String tossSecret, String tossApiUrl, String apiName,
        SaveAmountRequest amountRequest) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(tossApiUrl + apiName))
            .header("Authorization", "Basic " + PaymentUtil.getPayAuth(tossSecret))
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString(PaymentUtil.requestBody(amountRequest)))
            .build();

        HttpResponse httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return httpResponse;
    }

}
