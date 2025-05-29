package com.reservemate.reserve_mate_backend.admin.reservation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.DashboardReservationResponse;
import com.reservemate.reserve_mate_backend.admin.reservation.service.AdminReservationService;
import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/reservation")
@RequiredArgsConstructor
public class AdminReservationController {

    private final AdminReservationService reservationService;

    @GetMapping("/dashboardReservations")
    public ResponseEntity<List<DashboardReservationResponse>> getDashboardReservations(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(reservationService.getDashboardReservations(userDetails.getId()));
    }

}
