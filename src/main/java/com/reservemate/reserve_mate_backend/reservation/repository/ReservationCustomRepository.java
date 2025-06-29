package com.reservemate.reserve_mate_backend.reservation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.AdminReservationResponse;
import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.DashboardReservationResponse;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;
import com.reservemate.reserve_mate_backend.reservation.dto.response.ReservationsResponse;

public interface ReservationCustomRepository {

    // 관리자 예약 현황
    Slice<AdminReservationResponse> getAdminReservations(Long userId, String searchTerm,
        ReservationStatus reservationStatus, Long facilityId, LocalDate searchDate, Pageable pageable);

    // 대시보드 최근 예약
    List<DashboardReservationResponse> getDashboardReservationResponse(Long userId, Long facilityId, Integer year,
        Integer month);

    /* 예약 내역 (past) */
    Slice<ReservationsResponse> findByUserAndStatusIn(Long userId, List<ReservationStatus> status, Pageable pageable);

}
