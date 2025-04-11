package com.reservemate.reserve_mate_backend.match.service;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.dto.request.CancelPlayerDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.payment.dto.request.ApplyPlayerDto;
import com.reservemate.reserve_mate_backend.user.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchPlayerService {

    private final MatchPlayerRepository matchPlayerRepository;

    /*
     * 매치 취소
     */
    @Transactional
    public void cancelMatchRequest(CancelPlayerDto cancelPlayerDto) {

        MatchPlayer matchPlayer = cancelPlayerDto.getMatchPlayer();

        matchPlayer.chgStatusCancel();

        Match match = matchPlayer.getMatch();

        int playerCnt = matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY);
        match.chgMatchStatus(playerCnt);
    }

    /*
     * 매치 신청
     */
    @Transactional
    public void applyForMatch(ApplyPlayerDto applyPlayerDto) {
        User user = applyPlayerDto.getUser();
        Match match = applyPlayerDto.getMatch();

        MatchPlayer matchPlayer = MatchPlayer.toMatchPlayer(user, match);

        matchPlayerRepository.save(matchPlayer);

        int playerCnt = matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY);
        match.chgMatchStatus(playerCnt);
    }

}
