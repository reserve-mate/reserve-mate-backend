package com.reservemate.reserve_mate_backend.admin.payment.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.admin.payment.service.AdminPaymentService;
import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/admin/payment")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final AdminPaymentService adminPaymentService;

    /* 관리자 대시보드 총 매출 */
    @GetMapping("/getTotalRevenues")
    public ResponseEntity<Integer> getTotalRevenues(@AuthenticationPrincipal CustomUserDetails customUserDetails,
        @RequestParam("facilityId") Long facilityId, @RequestParam("year") Integer year,
        @RequestParam("month") Integer month) {
        return ResponseEntity.ok(adminPaymentService.getTotalRevenues(customUserDetails.getId(), facilityId, year,
            month));
    }

}
