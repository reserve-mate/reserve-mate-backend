package com.reservemate.reserve_mate_backend.payment.service;

import java.net.http.HttpResponse;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.dto.request.CancelPayRequestDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.ConfirmRequestDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.PaymentHistReqDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResDto;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentResponse;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentCustomRepository;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
import com.reservemate.reserve_mate_backend.payment.util.PaymentUtil;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentCustomRepository paymentCustomRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    @Value("${toss.pay.clientkey}")
    private String tossClient;

    @Value("${toss.pay.secretkey}")
    private String tossSecret;

    @Value("${toss.pay.baseurl}")
    private String tossApiUrl;

    @Value("${toss.pay.successurl}")
    private String successUrl;

    @Value("${toss.pay.failurl}")
    private String failUrl;

    /* 매치 결제 내역 */
    public Slice<PaymentHistResDto> getPaymentHistory(PaymentHistReqDto histReqDto) {

        User user = userRepository.findById(histReqDto.getUserId())
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Pageable pageable = PageRequest.of(0, 10);

        Slice<PaymentHistResDto> payments = null;
        if (histReqDto.getPayType().equals("match")) {
            payments = paymentCustomRepository.getMatchPayHist(user.getId(), histReqDto.getPaymentStatus(), pageable);
        }

        if (histReqDto.getPayType().equals("reserve")) {
            // 추후 코드 작성 예정
            return null;
        }

        return payments;

    }

    /* 결제 취소  */
    @Transactional
    public PaymentResponse requestCancelPayment(CancelPayRequestDto cancelPayRequestDto) {

        Payment payment = paymentRepository.findByMerchantUid(cancelPayRequestDto.getPaymentKey())
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PAYMENT));
        payment.isPaid();

        try {
            int refundAmount = payment.refundAmount();

            HttpResponse response = PaymentUtil.requestCancelPay(tossSecret, tossApiUrl + cancelPayRequestDto
                .getPaymentKey(), "/cancel", cancelPayRequestDto.getCancelReason(), refundAmount);
            if (response.statusCode() == 200) {
                payment.cancel(cancelPayRequestDto.getCancelReason(), refundAmount);
            } else {
                JSONObject errorResponse = Utils.stringToJson(response.body().toString());
                return PaymentResponse.toErrorResponse(errorResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }

        return PaymentResponse.toPaymentResponse(payment, successUrl, failUrl);
    }

    // 결제 요청
    @Transactional
    public PaymentResponse requestPayment(ConfirmRequestDto confirmRequestDto) {

        User user = userRepository.findById(confirmRequestDto.getUserId())
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Match match = matchRepository.findById(confirmRequestDto.getMatchId())
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));
        match.isEndMatch();
        match.isFinish();
        match.validatePrice(confirmRequestDto.getAmount());

        boolean isExistPayment = paymentRepository.existsPayment(user.getId(), match.getMatchId());

        if (isExistPayment)
            throw new ApiException(ErrorCode.DUPLICATION_PAYMENT);

        Payment payment = confirmRequestDto.toEntity(confirmRequestDto, match, user);
        payment = paymentRepository.save(payment);

        return PaymentResponse.toPaymentResponse(payment, confirmRequestDto.getSuccessUrl(), confirmRequestDto
            .getFailUrl());
    }

    // 결제 최종 승인 후 데이터 처리
    @Transactional
    public PaymentResponse requestPayConfirm(SaveAmountRequest amountRequest) {
        Payment payment = paymentRepository.findByImpUid(amountRequest.getOrderId())
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PAYMENT));
        payment.isPaidNo();

        // toss payments에 결제 요청
        try {
            HttpResponse httpResponse = amountRequest.requestPay(tossSecret, tossApiUrl, "confirm");

            if (httpResponse.statusCode() != 200) {
                PaymentUtil.requestCancelPay(tossSecret, tossApiUrl, amountRequest.getPaymentKey() + "/cancel",
                    failUrl, payment.getAmount());
                String failMsg = amountRequest.getFailReason(httpResponse.body().toString());
                payment.cancel(failMsg, payment.getAmount());
            } else {
                payment.verifyPayment(amountRequest.getAmount());
                payment.markAsPaid(amountRequest.getPaymentKey());
            }

        } catch (Exception e) {
            payment.markAsFailed();
            throw new ApiException(ErrorCode.PAYMETN_ERROR);
        }

        return PaymentResponse.toPaymentResponse(payment, successUrl, failUrl);

    }

}
