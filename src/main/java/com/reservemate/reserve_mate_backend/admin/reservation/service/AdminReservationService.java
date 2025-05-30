package com.reservemate.reserve_mate_backend.admin.reservation.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.AdminReservationResponse;
import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.DashboardReservationResponse;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;
import com.reservemate.reserve_mate_backend.reservation.repository.ReservationCustomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReservationService {

    private final ReservationCustomRepository reservationCustomRepository;

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
