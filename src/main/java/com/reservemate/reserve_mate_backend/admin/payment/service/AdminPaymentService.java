package com.reservemate.reserve_mate_backend.admin.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.validator.MatchValidator;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.validator.Validator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminPaymentService {

    private final PaymentRepository paymentRepository;
    private final FacilityManagerRepository facilityManagerRepository;
    private final CourtRepository courtRepository;
    private final Validator validator;
    private final MatchValidator matchValidator;

    /* 관리자 대시보드 총 매출 */
    public Integer getTotalRevenues(Long userId) {
        List<FacilityManager> managers = facilityManagerRepository.findByUserId(userId);
        List<Long> facilityIds = FacilityManager.getFacilityIds(managers);
        List<Court> courts = courtRepository.findByFacilityIds(facilityIds);

        List<Reservation> reservations = validator.getCourtsReservations(courts);   // 예약 리스트
        List<Match> matches = matchValidator.getCourtsMatches(courts);              // 매치 리스트

        List<Payment> reservationPayments = paymentRepository.findByReservationIn(reservations);
        List<Payment> matchPayments = paymentRepository.findByMatchIn(matches);

        return Payment.getTotalRevenues(reservationPayments) + Payment.getTotalRevenues(matchPayments);
    }

}
