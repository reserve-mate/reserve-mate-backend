package com.reservemate.reserve_mate_backend.admin.match.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.PlayerEjectRequest;
import com.reservemate.reserve_mate_backend.admin.match.service.AdminMatchPlayerService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/admin/player")
@RequiredArgsConstructor
public class AdminMatchPlayerController {

    private final AdminMatchPlayerService adminMatchPlayerService;

    @PutMapping("/eject/{playerId}")
    public ResponseEntity<Void> postMethodName(HttpServletRequest request, @PathVariable("playerId") Long playerId,
        @RequestBody PlayerEjectRequest ejectRequest) {
        adminMatchPlayerService.removePlayerFromMatch(request, playerId, ejectRequest);
        return ResponseEntity.ok().build();
    }

}
