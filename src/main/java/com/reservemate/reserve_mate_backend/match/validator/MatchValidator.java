package com.reservemate.reserve_mate_backend.match.validator;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Component;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MatchValidator {

    private final MatchRepository matchRepository;
    private final MatchPlayerRepository matchPlayerRepository;

    // 해당 매치에 대한 리뷰 검사
    public MatchPlayer getMatchCompleteChk(Long matchId, Long userId) {
        MatchPlayer matchPlayer = matchPlayerRepository.findByMatchIdAndUserIdAndStatus(matchId, userId,
            PlayerStatus.COMPLETED)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PLAYER));
        matchPlayer.isNotEndMatch();

        return matchPlayer;
    }

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
