package com.reservemate.reserve_mate_backend.admin.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.AdminReservationDetailResponse;
import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.AdminReservationResponse;
import com.reservemate.reserve_mate_backend.admin.reservation.dto.response.DashboardReservationResponse;
import com.reservemate.reserve_mate_backend.admin.reservation.service.AdminReservationCUDService;
import com.reservemate.reserve_mate_backend.admin.reservation.service.AdminReservationService;
import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;

@Controller
@RequestMapping("/admin/reservation")
@RequiredArgsConstructor
public class AdminReservationController {

    private final AdminReservationService reservationService;
    private final AdminReservationCUDService adminReservationCUDService;

    @GetMapping("/getAdminTotalReservation")
    public ResponseEntity<Long> getAdminTotalReservation(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(reservationService.getAdminTotalReservation(customUserDetails.getId()));
    }
    

    @PutMapping("/status/{reservationId}")
    public ResponseEntity<Void> putMethodName(@PathVariable("reservationId") Long reservationId,
        @RequestParam(name = "status") ReservationStatus reservationStatus) {
        adminReservationCUDService.adminReservationCancel(reservationId, reservationStatus);
        return ResponseEntity.ok().build();
    }

    /* 관리자 예약 상세 */
    @GetMapping("/{reservationId}")
    public ResponseEntity<AdminReservationDetailResponse> getAdminReservaionDetail(
        @PathVariable("reservationId") Long reservationId) {
        return ResponseEntity.ok(reservationService.getAdminReservaionDetail(reservationId));
    }

    /* 관리자 예약 현황 목록 조회 */
    @GetMapping("/reservations")
    public ResponseEntity<Slice<AdminReservationResponse>> getReservations(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestParam(name = "searchTerm", required = false) String searchTerm,
        @RequestParam(name = "reserveStatus", required = false) ReservationStatus reserveStatus,
        @RequestParam(name = "facility", required = false) Long facilityId,
        @RequestParam(name = "searchDate", required = false) LocalDate searchDate,
        @RequestParam("pageNum") int pageNum) {
        return ResponseEntity.ok(reservationService.getAdminReservations(userDetails.getId(), searchTerm, reserveStatus,
            facilityId, searchDate, pageNum));
    }

    /* 관리자 대시보드 최근 예약 */
    @GetMapping("/dashboardReservations")
    public ResponseEntity<List<DashboardReservationResponse>> getDashboardReservations(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(reservationService.getDashboardReservations(userDetails.getId()));
    }

}
