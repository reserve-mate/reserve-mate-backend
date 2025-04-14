package com.reservemate.reserve_mate_backend.payment.service;

import java.net.http.HttpResponse;

import org.json.simple.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.exception.TossApiException;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.dto.request.ApplyPlayerDto;
import com.reservemate.reserve_mate_backend.match.dto.request.CancelPlayerDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.payment.client.PayClient;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.dto.request.CancelPayRequestDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.CancelPaymentDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.PaymentHistReqDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.RequestPaymentDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResDto;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentResponse;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentCustomRepository;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentCustomRepository paymentCustomRepository;
    private final UserRepository userRepository;
    private final MatchPlayerRepository matchPlayerRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final PayClient payClient;

    /* 매치 삭제 시 일괄 삭제 */
    @Transactional
    public void bulkPaymentCancel() {

    }

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

    /* 매치 취소 검증 후 결제 취소 */
    @Transactional
    public void cancelPayment(CancelPaymentDto cancelPaymentDto) throws Exception {
        User user = cancelPaymentDto.getUser();
        Match match = cancelPaymentDto.getMatch();

        Payment payment = paymentRepository.findByMatchAndUser(match, user)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PAYMENT));
        payment.isPaid();

        int refundAmount = payment.refundAmount();

        HttpResponse response = payClient.requestCancelPay(payment.getMerchantUid(), cancelPaymentDto
            .getCancelReason(), refundAmount);
        if (response.statusCode() == 200) {
            payment.cancel(cancelPaymentDto.getCancelReason(), refundAmount);
        } else {
            JSONObject errorResponse = Utils.stringToJson(response.body().toString());
            String code = errorResponse.get("code") != null ? errorResponse.get("code").toString() : "400";
            String message = errorResponse.get("message") != null ? errorResponse.get("message").toString()
                : "처리 중 에러가 발생하였습니다.";

            throw new TossApiException(code, message);
        }
    }

    /* 결제 취소  */
    @Transactional
    public PaymentResponse requestCancelPayment(CancelPayRequestDto cancelPayRequestDto) {

        Payment payment = paymentRepository.findByMerchantUid(cancelPayRequestDto.getPaymentKey())
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PAYMENT));
        payment.isPaid();

        MatchPlayer matchPlayer = matchPlayerRepository.findByUserAndMatch(payment.getUser(), payment.getMatch())
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PLAYER));
        matchPlayer.isFinish();
        matchPlayer.isCanCancel();

        try {
            int refundAmount = payment.refundAmount();

            HttpResponse response = payClient.requestCancelPay(cancelPayRequestDto.getPaymentKey(), cancelPayRequestDto
                .getCancelReason(), refundAmount);
            if (response.statusCode() == 200) {
                payment.cancel(cancelPayRequestDto.getCancelReason(), refundAmount);
                eventPublisher.publishEvent(new CancelPlayerDto(matchPlayer));
            } else {
                JSONObject errorResponse = Utils.stringToJson(response.body().toString());
                return PaymentResponse.toErrorResponse(errorResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }

        return PaymentResponse.toCancelResponse(payment.getImpUid(), payment.getCancelReason(), payment.getStatus());
    }

    /* 매치 검증 후 결제 요청 */
    @Transactional
    public void requestPayment(RequestPaymentDto requestPaymentDto) {
        User user = requestPaymentDto.getUser();
        Match match = requestPaymentDto.getMatch();

        /* 결제를 한 이력이 있는지 검증 */
        boolean isExistPayment = paymentRepository.existsPayment(user.getId(), match.getMatchId());

        if (isExistPayment)
            throw new ApiException(ErrorCode.DUPLICATION_PAYMENT);

        Payment payment = requestPaymentDto.toEntity();
        paymentRepository.save(payment);
    }

    // 결제 최종 승인 후 데이터 처리
    @Transactional
    public PaymentResponse requestPayConfirm(SaveAmountRequest amountRequest) {
        Payment payment = paymentRepository.findByImpUid(amountRequest.getOrderId())
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PAYMENT));
        payment.isNotReady();
        payment.verifyPayment(amountRequest.getAmount());

        PaymentResponse response = null;
        // toss payments에 결제 요청
        try {
            HttpResponse httpResponse = payClient.requestPay(amountRequest);
            if (httpResponse.statusCode() != 200) {
                String failMsg = amountRequest.getFailReason(httpResponse.body().toString());
                payClient.requestCancelPay(amountRequest.getPaymentKey(), failMsg, payment.getAmount());
                payment.cancel(failMsg, payment.getAmount());

                response = PaymentResponse.toCancelResponse(amountRequest.getOrderId(), failMsg, payment.getStatus());
            } else {
                payment.markAsPaid(amountRequest.getPaymentKey());
                eventPublisher.publishEvent(new ApplyPlayerDto(payment.getUser(), payment.getMatch()));

                response = PaymentResponse.toPaymentConfirm(payment);
            }

        } catch (Exception e) {
            e.printStackTrace();
            payment.markAsFailed();
            throw new ApiException(ErrorCode.PAYMETN_ERROR);
        }

        return response;

    }

}
