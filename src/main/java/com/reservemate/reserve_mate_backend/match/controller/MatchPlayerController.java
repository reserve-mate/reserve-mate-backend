package com.reservemate.reserve_mate_backend.match.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.match.dto.request.ApplyMatchDto;
import com.reservemate.reserve_mate_backend.match.service.MatchPlayerService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class MatchPlayerController {

    private final MatchPlayerService matchPlayerService;

    @PutMapping("/cancelMatch/{matchId}")
    public void cancelMatchRequest(@PathVariable(name = "matchId") Long matchId,
        @RequestParam(name = "userId") Long userId) {
        matchPlayerService.cancelMatchRequest(matchId, userId);
    }

    @PostMapping("/apply")
    public void applyForMatch(@RequestBody ApplyMatchDto applyMatchDto) {
        matchPlayerService.applyForMatch(applyMatchDto);
    }

}
