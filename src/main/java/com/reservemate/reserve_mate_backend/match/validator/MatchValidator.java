package com.reservemate.reserve_mate_backend.match.validator;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchValidator {

    private final MatchRepository matchRepository;

    // 코트와 관련된 매치 조회
    public List<Match> getCourtsMatches(List<Court> courts, Integer year, Integer month, Long facilityId) {
        List<Long> courtIds = Court.getCourtIds(courts);
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, Utils.getLastMonthDay(year, month));

        if (facilityId == 0L) {
            return matchRepository.findByCourtIdsMatchDate(courtIds, startDate, endDate);
        } else {
            return matchRepository.findByCourtIdsMatchDateFacility(courtIds, startDate, endDate, facilityId);
        }

    }

}
