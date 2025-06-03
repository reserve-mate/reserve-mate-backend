package com.reservemate.reserve_mate_backend.admin.match.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.PlayerEjectRequest;
import com.reservemate.reserve_mate_backend.admin.match.service.AdminMatchPlayerService;
import com.reservemate.reserve_mate_backend.common.auth.service.CustomUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/admin/player")
@RequiredArgsConstructor
public class AdminMatchPlayerController {

    private final AdminMatchPlayerService adminMatchPlayerService;

    @GetMapping("/getAdminMatchPlayerCount")
    public ResponseEntity<Integer> getAdminMatchPlayerCount(
        @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestParam("facilityId") Long facilityId,
        @RequestParam("year") Integer year, @RequestParam("month") Integer month) {
        return ResponseEntity.ok(adminMatchPlayerService.getAdminMatchPlayerCount(customUserDetails.getId(), facilityId,
            year, month));
    }

    // 참가자 퇴장
    @PutMapping("/eject/{playerId}")
    public ResponseEntity<Void> postMethodName(HttpServletRequest request, @PathVariable("playerId") Long playerId,
        @RequestBody PlayerEjectRequest ejectRequest) {
        adminMatchPlayerService.removePlayerFromMatch(request, playerId, ejectRequest);
        return ResponseEntity.ok().build();
    }

}
