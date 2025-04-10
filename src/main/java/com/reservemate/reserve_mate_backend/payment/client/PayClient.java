package com.reservemate.reserve_mate_backend.payment.client;

import java.io.IOException;
import java.net.http.HttpResponse;

import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;

public interface PayClient {

    HttpResponse requestPay(String tossSecret, String tossApiUrl, String apiName,
        SaveAmountRequest amountRequest) throws IOException, InterruptedException;

}
