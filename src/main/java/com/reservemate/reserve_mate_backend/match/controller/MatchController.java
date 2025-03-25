package com.reservemate.reserve_mate_backend.match.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.match.dto.request.CreateMatchDto;
import com.reservemate.reserve_mate_backend.match.service.MatchService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @GetMapping("/{matchId}")
    public String getMatch(@PathVariable(name = "matchId") Long matchId) {
        return new String();
    }

    @PostMapping("/registMatch")
    public void registMatch(@Valid @RequestBody CreateMatchDto createMatchDto) {
        matchService.registMatch(createMatchDto);
    }

}
