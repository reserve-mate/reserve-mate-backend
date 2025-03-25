package com.reservemate.reserve_mate_backend.match.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.match.dto.request.ApplyMatchDto;
import com.reservemate.reserve_mate_backend.match.service.MatchPlayerService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/player")
@RequiredArgsConstructor
public class MatchPlayerController {

    private final MatchPlayerService matchPlayerService;

    @PostMapping("/apply")
    public void applyForMatch(@RequestBody ApplyMatchDto applyMatchDto) {
        matchPlayerService.applyForMatch(applyMatchDto);
    }

}
