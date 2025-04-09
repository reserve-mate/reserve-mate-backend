package com.reservemate.reserve_mate_backend.match.service;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.dto.request.ApplyPlayerDto;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchPlayerService {

    private final MatchPlayerRepository matchPlayerRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    /*
     * 매치 취소
     */
    @Transactional
    public void cancelMatchRequest(Long matchId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new IllegalArgumentException("매치 정보가 존재하지 않습니다."));

        MatchPlayer matchPlayer = matchPlayerRepository.findByUserAndMatch(user, match)
            .orElseThrow(() -> new IllegalArgumentException("매치 신청 내역이 존재하지 않습니다."));

        match.isFinish();
        matchPlayer.isCanCancel();

        // TODO : 결제 취소 기능 필요
        matchPlayer.chgStatusCancel();

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
