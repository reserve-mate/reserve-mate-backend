package com.reservemate.reserve_mate_backend.payment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.reservemate.reserve_mate_backend.payment.domain.PaymentStatus;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResDto;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResponse;

public interface PaymentCustomRepository {

    Slice<PaymentHistResDto> getMatchPayHist(Long id, PaymentStatus paymentStatus, Pageable pageable);

    Slice<PaymentHistResponse> getPaymentHist(Long userId, String type, Pageable pageable);

}
