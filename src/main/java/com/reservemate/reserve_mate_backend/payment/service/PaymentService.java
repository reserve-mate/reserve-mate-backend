package com.reservemate.reserve_mate_backend.payment.service;

import java.net.http.HttpResponse;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.dto.request.ApplyPlayerDto;
import com.reservemate.reserve_mate_backend.match.dto.request.CancelPlayerDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.client.PayClient;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentStatus;
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

import jakarta.servlet.http.HttpServletRequest;
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
    private final MatchRepository matchRepository;

    private final ApplicationEventPublisher eventPublisher;
    private final PayClient payClient;
    private final JwtUtil jwtUtil;

    /* 매치 삭제 후 각 플레이어 환불 */
    @Transactional
    public void matchCancelPayment(List<MatchPlayer> players) {

        for (MatchPlayer matchPlayer : players) {
            Payment payment = paymentRepository.findByMatchAndUserAndStatus(matchPlayer.getMatch(), matchPlayer
                .getUser(), PaymentStatus.PAID)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PAYMENT));

            try {
                String cancelReason = "매치 취소에 따른 환불 처리";
                int refundAmount = payment.getAmount();
                HttpResponse response = payClient.requestCancelPay(payment.getMerchantUid(), cancelReason,
                    refundAmount);
                if (response.statusCode() == 200) {
                    payment.refund(cancelReason);
                    matchPlayer.chgMatchRemoved();
                } else {
                    JSONObject errorResponse = Utils.stringToJson(response.body().toString());
                    String message = errorResponse.get("message") != null ? errorResponse.get("message").toString()
                        : "처리 중 에러가 발생하였습니다.";

                    throw new IllegalArgumentException(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new ApiException(ErrorCode.PAYMETN_CANCEL_ERROR);
            }

        }

    }

    /* 매치 결제 취소 상태 체크 */
    public PaymentResponse checkCancelStatus(String orderId) {
        if (orderId == null || orderId.isBlank()) {
            throw new ApiException(ErrorCode.MISSING_QUERY_PARAM);
        }

        Payment payment = paymentRepository.findByImpUid(orderId).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_PAYMENT));
        payment.isCancel();

        List<MatchPlayer> matchPlayers = matchPlayerRepository.findByMatchAndUser(payment.getMatch(), payment
            .getUser());
        if (matchPlayers.isEmpty()) {
            throw new ApiException(ErrorCode.NOT_FOUND_PAYMENT);
        } else if (matchPlayers.get(0).getStatus() != PlayerStatus.CANCEL) {
            throw new ApiException(ErrorCode.NOT_CANCEL_PAYMENT);
        }

        PaymentResponse response = PaymentResponse.toPaymentCancel(payment.getImpUid(), payment.getCancelReason());
        return response;
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
        Payment payment = paymentRepository.findByImpUid(cancelPaymentDto.getOrderId())
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PAYMENT));
        payment.isPaid();

        int refundAmount = payment.refundAmount();

        HttpResponse response = payClient.requestCancelPay(payment.getMerchantUid(), cancelPaymentDto
            .getCancelReason(), refundAmount);
        if (response.statusCode() == 200) {
            payment.cancel(cancelPaymentDto.getCancelReason(), refundAmount);
        } else {
            JSONObject errorResponse = Utils.stringToJson(response.body().toString());
            String message = errorResponse.get("message") != null ? errorResponse.get("message").toString()
                : "처리 중 에러가 발생하였습니다.";

            throw new IllegalArgumentException(message);
        }
    }

    /* 결제 취소  */
    @Transactional
    public PaymentResponse requestCancelPayment(CancelPayRequestDto cancelPayRequestDto) {

        Payment payment = paymentRepository.findByMerchantUid(cancelPayRequestDto.getPaymentKey())
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PAYMENT));
        payment.isPaid();

        MatchPlayer matchPlayer = matchPlayerRepository.findByUserAndMatchAndStatus(payment.getUser(), payment
            .getMatch(), PlayerStatus.READY)
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
                return PaymentResponse.toPaymentFailed(errorResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.SERVER_ERROR);
        }

        return PaymentResponse.toPaymentCancel(payment.getImpUid(), payment.getCancelReason());
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
    public PaymentResponse requestPayConfirm(HttpServletRequest request, SaveAmountRequest amountRequest) {

        String accessToken = request.getHeader("access");
        Long userId = jwtUtil.getId(accessToken);

        Match match = matchRepository.findByIdWithLock(amountRequest.getMatchId())
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));
        match.validatePrice(amountRequest.getAmount());
        match.isFinish();

        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        PaymentResponse response = null;
        // toss payments에 결제 요청
        try {
            HttpResponse httpResponse = payClient.requestPay(amountRequest.getOrderId(), amountRequest.getPaymentKey(),
                amountRequest.getAmount());
            if (httpResponse.statusCode() != 200) { // 결제 승인 시 에러로 인한 취소는 DB에 넣지 않음
                String failMsg = amountRequest.getFailReason(httpResponse.body().toString());
                payClient.requestCancelPay(amountRequest.getPaymentKey(), failMsg, amountRequest.getAmount());

                response = PaymentResponse.toPaymentCancel(amountRequest.getOrderId(), failMsg);
            } else {

                Payment payment = amountRequest.toEntity(match, user);
                paymentRepository.save(payment);
                eventPublisher.publishEvent(new ApplyPlayerDto(payment.getUser(), payment.getMatch()));

                response = PaymentResponse.toMatchPaymentResponse(match);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.PAYMETN_ERROR);
        }

        return response;

    }

}
