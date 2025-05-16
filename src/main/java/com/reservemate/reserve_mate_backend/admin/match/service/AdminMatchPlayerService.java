package com.reservemate.reserve_mate_backend.admin.match.service;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.match.domain.EjectionReason;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMatchPlayerService {

    private final MatchPlayerRepository matchPlayerRepository;

    /* 매치 퇴장 */
    @Transactional
    public void removePlayerFromMatch(Long playerId, EjectionReason removalReason) {
        MatchPlayer matchPlayer = matchPlayerRepository.findById(playerId)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PLAYER));
        matchPlayer.isOngoingPlayer();

        Match match = matchPlayer.getMatch();
        match.isNotOngoinChk();

        matchPlayer.removePlayer(removalReason);
    }

}
