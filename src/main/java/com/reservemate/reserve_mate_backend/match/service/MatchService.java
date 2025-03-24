package com.reservemate.reserve_mate_backend.match.service;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;

}
