package com.reservemate.reserve_mate_backend.payment.client.impl;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.payment.client.PayClient;
import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;
import com.reservemate.reserve_mate_backend.payment.util.PaymentUtil;

@Component
public class PayClientImpl implements PayClient {

    @Value("${toss.pay.clientkey}")
    private String tossClient;

    @Value("${toss.pay.secretkey}")
    private String tossSecret;

    @Value("${toss.pay.baseurl}")
    private String tossApiUrl;

    @Override
    public HttpResponse requestPay(SaveAmountRequest amountRequest) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(tossApiUrl + "confirm"))
            .header("Authorization", "Basic " + PaymentUtil.getPayAuth(tossSecret))
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString(PaymentUtil.requestBody(amountRequest)))
            .build();

        HttpResponse httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return httpResponse;
    }

    @Override
    public HttpResponse requestPayForTest(String scretKey, String url,
        SaveAmountRequest amountRequest) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url + "confirm"))
            .header("Authorization", "Basic " + PaymentUtil.getPayAuth(scretKey))
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString(PaymentUtil.requestBody(amountRequest)))
            .build();

        HttpResponse httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return httpResponse;
    }

    @Override
    public HttpResponse requestCancelPay(String paymentKey, String cancelReason,
        int cancelAmount) throws IOException, InterruptedException {
        String jsonBody = String.format("{\"cancelReason\":\"%s\", \"cancelAmount\":%d}", cancelReason, cancelAmount);
        if (cancelAmount == 0)
            jsonBody = String.format("{\"cancelReason\":\"%s\"}", cancelReason);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(tossApiUrl + paymentKey + "/cancel"))
            .header("Authorization", "Basic " + PaymentUtil.getPayAuth(tossSecret))
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        HttpResponse httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        return httpResponse;
    }

}
