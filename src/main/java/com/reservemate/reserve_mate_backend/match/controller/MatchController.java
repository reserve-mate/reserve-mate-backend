package com.reservemate.reserve_mate_backend.match.controller;

import org.springframework.web.bind.annotation.RestController;

import com.reservemate.reserve_mate_backend.match.service.MatchService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/match")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/test")
    public String postMethodName(@RequestBody String entity) {
        
        return entity;
    }
    

}
