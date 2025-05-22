package com.reservemate.reserve_mate_backend.reservation.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.reservation.dto.request.CreateReservation;
import com.reservemate.reserve_mate_backend.reservation.service.ReserveCUDService;
import com.reservemate.reserve_mate_backend.reservation.service.ReserveService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reserve")
public class ReservceController {

    private final ReserveService reserveService;
    private final ReserveCUDService reserveCUDService;

    @PostMapping("/saveReservation")
    public ResponseEntity<Void> postMethodName(HttpServletRequest request,
        @RequestBody CreateReservation CreateReservation) {
        reserveCUDService.createReservation(request, CreateReservation);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reserveHours")
    public ResponseEntity<List<LocalTime>> getAvailableTimeSlots(@RequestParam("courtId") Long courtId,
        @RequestParam("reserveDate") LocalDate reserveDate) {
        return ResponseEntity.ok(reserveService.getAvailableTimeSlots(courtId, reserveDate));
    }

}
