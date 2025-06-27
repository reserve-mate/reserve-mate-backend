package com.reservemate.reserve_mate_backend.reservation.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.reservation.dto.request.CreateReservation;
import com.reservemate.reserve_mate_backend.reservation.dto.response.ReservationDetailResponse;
import com.reservemate.reserve_mate_backend.reservation.dto.response.ReservationsResponse;
import com.reservemate.reserve_mate_backend.reservation.service.ReserveCUDService;
import com.reservemate.reserve_mate_backend.reservation.service.ReserveService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reserve")
public class ReservceController {

    private final ReserveService reserveService;
    private final ReserveCUDService reserveCUDService;

    /* 예약 취소 */
    @PutMapping("/cancel/{reservationId}")
    public ResponseEntity<String> putMethodName(@PathVariable("reservationId") Long id,
        @RequestParam("cancelReason") String cancelReason) {
        return ResponseEntity.ok(reserveCUDService.reservationCancel(id, cancelReason));
    }

    /* 예약 취소(대기 상태) */
    @GetMapping("/verifyReservation")
    public ResponseEntity<Boolean> verifyReservation(@RequestParam("reservationId") Long reservationId) {
        return ResponseEntity.ok(reserveService.verifyReservation(reservationId));
    }

    /* 예약 목록 조회 */
    @GetMapping("/reservations")
    public ResponseEntity<Slice<ReservationsResponse>> getMethodName(HttpServletRequest request,
        @RequestParam("type") String type, @RequestParam("pageNum") Integer pageNum) {
        return ResponseEntity.ok(reserveService.getReservations(request, type, pageNum));
    }

    /* 예약 상세 */
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailResponse> getReservationDetail(
        @PathVariable("reservationId") Long reservationId) {
        return ResponseEntity.ok(reserveService.getReservationDetail(reservationId));
    }

    /* 예약(대기) 생성 */
    @PostMapping("/saveReservation")
    public ResponseEntity<Void> postMethodName(HttpServletRequest request,
        @RequestBody CreateReservation CreateReservation) {
        reserveCUDService.createReservation(request, CreateReservation);
        return ResponseEntity.ok().build();
    }

    /* 사용 가능한 시간대 조회 */
    @GetMapping("/reserveHours")
    public ResponseEntity<List<LocalTime>> getAvailableTimeSlots(@RequestParam("courtId") Long courtId,
        @RequestParam("reserveDate") LocalDate reserveDate) {
        return ResponseEntity.ok(reserveService.getAvailableTimeSlots(courtId, reserveDate));
    }

}
