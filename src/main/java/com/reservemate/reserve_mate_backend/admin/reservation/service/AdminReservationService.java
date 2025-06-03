package com.reservemate.reserve_mate_backend.admin.reservation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.AdminReservationDetailResponse;
import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.AdminReservationResponse;
import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.DashboardReservationResponse;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;
import com.reservemate.reserve_mate_backend.reservation.repository.ReservationCustomRepository;
import com.reservemate.reserve_mate_backend.reservation.repository.ReserveRepository;
import com.reservemate.reserve_mate_backend.reservation.validator.Validator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReservationService {

    private final ReservationCustomRepository reservationCustomRepository;
    private final ReserveRepository reserveRepository;
    private final PaymentRepository paymentRepository;
    private final FacilityManagerRepository facilityManagerRepository;
    private final CourtRepository courtRepository;
    private final Validator validator;

    // 관리자 대시보드 총 예약 수
    public Long getAdminTotalReservation(Long userId) {
        List<FacilityManager> managers = facilityManagerRepository.findByUserId(userId);
        List<Long> facilityIds = FacilityManager.getFacilityIds(managers);
        List<Court> courts = courtRepository.findByFacilityIds(facilityIds);

        List<Reservation> reservations = validator.getCourtsReservations(courts); 

        return Reservation.getTotalReservation(reservations);
    }

    // 관리자 예약 상세
    public AdminReservationDetailResponse getAdminReservaionDetail(Long reservationId) {
        Reservation reservation = reserveRepository.findById(reservationId).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_RESERVATION));

        AdminReservationDetailResponse response = null;

        if (reservation.getStatus() == ReservationStatus.PENDING) {  // 대기 상태인 경우
            response = AdminReservationDetailResponse.getReservationPending(reservation);
        } else if (reservation.getStatus() == ReservationStatus.CANCELED) {
            Optional<Payment> payment = paymentRepository.findByReservation(reservation);
            response = AdminReservationDetailResponse.getReservationCancel(reservation, payment);
        } else {
            Payment payment = paymentRepository.findByReservation(reservation).orElseThrow(() -> new ApiException(
                ErrorCode.NOT_FOUND_RESERVATION));
            response = AdminReservationDetailResponse.getReservationPayment(reservation, payment);
        }

        return response;
    }

    // 관리자 예약 현황
    public Slice<AdminReservationResponse> getAdminReservations(Long userId, String searchTerm,
        ReservationStatus reservationStatus, Long facilityId, LocalDate searchDate, int pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 6);
        Slice<AdminReservationResponse> response = reservationCustomRepository.getAdminReservations(userId, searchTerm,
            reservationStatus, facilityId, searchDate, pageable);
        return response;
    }

    // 관리자 대시보드 예약 목록
    public List<DashboardReservationResponse> getDashboardReservations(Long userId) {

        List<DashboardReservationResponse> responses = reservationCustomRepository.getDashboardReservationResponse(
            userId);

        return responses;
    }

}
