package com.reservemate.reserve_mate_backend.payment.dto.request;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.reservemate.reserve_mate_backend.payment.domain.PaymentMethod;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentType;
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

    private PaymentMethod paymentMethod;
    private Integer amount;
    private String orderId;
    private PaymentType paymentType;

    private String successUrl;
    private String failUrl;
    private String paymentKey;

    public HttpResponse requestPay(String tossSecret, String tossApiUrl,
        String apiName) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(tossApiUrl + apiName))
            .header("Authorization", "Basic " + PaymentUtil.getPayAuth(tossSecret))
            .header("Content-Type", "application/json")
            .method("POST", HttpRequest.BodyPublishers.ofString(PaymentUtil.requestBody(this)))
            .build();

        HttpResponse httpResponse = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        ;

        return httpResponse;
    }

}
