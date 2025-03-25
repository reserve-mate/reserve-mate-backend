package com.reservemate.reserve_mate_backend.match.service;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.dto.request.ApplyMatchDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
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

    @Transactional
    public void applyForMatch(ApplyMatchDto applyMatchDto){
        User user = userRepository.findById(applyMatchDto.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));
        
        Match match = matchRepository.findById(applyMatchDto.getMatchId())
            .orElseThrow(() -> new IllegalArgumentException("매치 정보가 존재하지 않습니다."));

        boolean isExist = matchPlayerRepository.existsByUserAndMatchAndStatusNot(user, match, PlayerStatus.CANCEL);

        if(!isExist){
            MatchPlayer matchPlayer = applyMatchDto.toMatchPlayer(user, match);
            matchPlayerRepository.save(matchPlayer);
        }else{
            throw new IllegalArgumentException("이미 매치 신청 내역이 존재합니다.");
        }
    }

}
