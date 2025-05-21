package com.reservemate.reserve_mate_backend.reservation.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.reservation.service.ReserveService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reserve")
public class ReservceController {

    private final ReserveService reserveService;

    @GetMapping("/reserveHours")
    public ResponseEntity<List<LocalTime>> getAvailableTimeSlots(@RequestParam("courtId") Long courtId,
        @RequestParam("reserveDate") LocalDate reserveDate) {
        return ResponseEntity.ok(reserveService.getAvailableTimeSlots(courtId, reserveDate));
    }

}
