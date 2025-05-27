package com.reservemate.reserve_mate_backend.payment.client;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;

public interface PayClient {

    // 파라미터 수정본
    HttpResponse requestPay(String impUid, String paymentKey, Integer amount) throws IOException, InterruptedException;

    HttpResponse requestPay(SaveAmountRequest amountRequest) throws IOException, InterruptedException;

    HttpResponse requestPayForTest(String scretKey, String url,
        SaveAmountRequest amountRequest) throws IOException, InterruptedException;

    HttpResponse requestCancelPay(String paymentKey, String cancelReason,
        int cancelAmount) throws IOException, InterruptedException;

}
