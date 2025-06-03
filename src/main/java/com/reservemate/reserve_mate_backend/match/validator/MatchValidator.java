package com.reservemate.reserve_mate_backend.match.validator;

import java.util.List;

import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchValidator {

    private final MatchRepository matchRepository;

    // 코트와 관련된 매치 조회
    public List<Match> getCourtsMatches(List<Court> courts) {
        List<Long> courtIds = Court.getCourtIds(courts);
        return matchRepository.findByCourtIds(courtIds);
    }

}
