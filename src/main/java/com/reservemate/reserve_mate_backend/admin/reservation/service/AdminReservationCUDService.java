package com.reservemate.reserve_mate_backend.admin.reservation.service;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;
import com.reservemate.reserve_mate_backend.reservation.dto.request.ReservationCancelRequest;
import com.reservemate.reserve_mate_backend.reservation.repository.ReserveRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReservationCUDService {

    private final ReserveRepository reserveRepository;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    /* 관리자 예약 상태 변경 */
    @Transactional
    public void adminReservationCancel(Long reservationId, ReservationStatus reservationStatus) {
        Reservation reservation = reserveRepository.findById(reservationId).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_RESERVATION));
        reservation.isCompleteOrCancel();

        if (reservationStatus == ReservationStatus.COMPLETED || reservationStatus == ReservationStatus.CONFIRMED) {  // 완료로 상태 변경을 하는 경우
            Optional<Payment> payment = paymentRepository.findByReservation(reservation);
            if (!payment.isPresent()) {
                throw new ApiException(ErrorCode.NOT_FOUND_PAYMENT);
            }

            if (reservationStatus == ReservationStatus.CONFIRMED) {    // 확정으로 상태 변경을 하는 경우
                reservation.confirm();
            } else {         // 완료 상태로 변경하는 경우
                reservation.complete();
            }

        } else if (reservationStatus == ReservationStatus.CANCELED) { // 취소로 상태 변경을 하는 경우
            if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
                eventPublisher.publishEvent(new ReservationCancelRequest(reservationId, "사용자 요구에 의한 취소 요청"));
            }
            reservation.cancel("사용자 요구에 의한 취소 요청");
        }

    }

}
