package com.reservemate.reserve_mate_backend.match.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.match.dto.request.CancelMatchRequest;
import com.reservemate.reserve_mate_backend.match.dto.request.RequestMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchApplyResponse;
import com.reservemate.reserve_mate_backend.match.service.MatchPlayerService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class MatchPlayerController {

    private final MatchPlayerService matchPlayerService;

    @PostMapping("/requestApplyMatch")
    public ResponseEntity<MatchApplyResponse> requestApplyMatch(HttpServletRequest request,
        @RequestBody RequestMatchDto requestMatchDto) {
        return ResponseEntity.ok(matchPlayerService.requestApplyMatch(request, requestMatchDto));
    }

    @PutMapping("/cancelMatch")
    public ResponseEntity<Void> cancelMatch(@RequestBody CancelMatchRequest cancelMatchRequest) {
        matchPlayerService.cancelMatch(cancelMatchRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verifyMatch")
    public ResponseEntity<Boolean> verifyMatch(HttpServletRequest request, @RequestParam(name = "matchId") Long matchId,
        @RequestParam(name = "amount") int amount) {
        return ResponseEntity.ok(matchPlayerService.verifyApplyMatch(request, matchId, amount));
    }

}
