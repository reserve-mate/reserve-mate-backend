package com.reservemate.reserve_mate_backend.admin.payment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
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
    public Integer getTotalRevenues(Long userId, Long facilityId, Integer year, Integer month) {
        List<FacilityManager> managers = facilityManagerRepository.findByUserId(userId);
        List<Long> facilityIds = FacilityManager.getFacilityIds(managers);
        List<Court> courts = courtRepository.findByFacilityIds(facilityIds);

        List<Reservation> reservations = validator.getCourtsReservations(courts, year, month, facilityId);   // 예약 리스트
        List<Match> matches = matchValidator.getCourtsMatches(courts, year, month, facilityId);              // 매치 리스트

        List<Payment> reservationPayments = getReservationPayments(reservations);
        List<Payment> matchPayments = getMatchPayments(matches);

        return Payment.getTotalRevenues(reservationPayments) + Payment.getTotalRevenues(matchPayments);
    }

    /* 매치 목록 chunk 처리 */
    private List<Payment> getMatchPayments(List<Match> matches) {
        List<List<Match>> chunks = Lists.partition(matches, 50);
        List<Payment> results = new ArrayList<>();

        for (List<Match> chunk : chunks) {
            results.addAll(paymentRepository.findByMatchIn(chunk));
        }

        return results;
    }

    /* 예약 목록 chunk 처리 */
    private List<Payment> getReservationPayments(List<Reservation> reservations) {
        List<List<Reservation>> chunks = Lists.partition(reservations, 50);
        List<Payment> results = new ArrayList<>();

        for (List<Reservation> chunk : chunks) {
            results.addAll(paymentRepository.findByReservationIn(chunk));
        }

        return results;
    }

}
