package com.reservemate.reserve_mate_backend.reservation.repository;

import java.util.List;

import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.DashboardReservationResponse;

public interface ReservationCustomRepository {

    List<DashboardReservationResponse> getDashboardReservationResponse(Long userId);

}
