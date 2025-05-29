package com.reservemate.reserve_mate_backend.admin.reservation.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.DashboardReservationResponse;
import com.reservemate.reserve_mate_backend.reservation.repository.ReservationCustomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminReservationService {

    private final ReservationCustomRepository reservationCustomRepository;

    // 관리자 대시보드 예약 목록
    public List<DashboardReservationResponse> getDashboardReservations(Long userId) {

        List<DashboardReservationResponse> responses = reservationCustomRepository.getDashboardReservationResponse(
            userId);

        return responses;
    }

}
