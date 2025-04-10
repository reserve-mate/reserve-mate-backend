package com.reservemate.reserve_mate_backend.payment.client;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;

public interface PayClient {

    HttpResponse requestPay(SaveAmountRequest amountRequest) throws IOException, InterruptedException;

    HttpResponse requestPayForTest(String scretKey, String url,
        SaveAmountRequest amountRequest) throws IOException, InterruptedException;

    HttpResponse requestCancelPay(String paymentKey, String cancelReason,
        int cancelAmount) throws IOException, InterruptedException;

}
