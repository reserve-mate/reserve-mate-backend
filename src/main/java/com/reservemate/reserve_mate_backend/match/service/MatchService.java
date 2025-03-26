package com.reservemate.reserve_mate_backend.match.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityImage;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityImageRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.dto.request.CreateMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.request.ModifyMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDetailDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final FacilityImageRepository facilityImageRepository;

    /*
     * 매치 정보 수정
     */
    @Transactional
    public void modifyMatch(Long matchId, ModifyMatchDto modifyMatchDto) {
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new IllegalArgumentException("매치가 정보가 존재하지 않습니다."));

        if (match.getMatchStatus() == MatchStatus.FINISH) {
            throw new IllegalArgumentException("이미 종료된 매치입니다.");
        }

        int playerCnt = matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY);

        if (playerCnt > modifyMatchDto.getTeamCapacity()) {
            throw new IllegalArgumentException("준비된 인원 수를 초과하는 값을 입력해 주세요.");
        }

        match.modifyMatch(modifyMatchDto.getTeamCapacity(), modifyMatchDto.getDescription());

    }

    /*
     * 매치 상세
     */
    public MatchDetailDto getMatch(Long matchId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new IllegalArgumentException("매치 정보가 존재하지 않습니다."));

        List<MatchPlayer> matchPlayers = matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY);

        List<FacilityImage> images = facilityImageRepository.findByFacility(match.getFacility());

        return MatchDetailDto.toMatchDetailDto(match, user, matchPlayers, images);
    }

    /*
     * 매치 등록
     */
    @Transactional
    public void registMatch(CreateMatchDto createMatchDto) {
        User user = userRepository.findById(createMatchDto.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("회원 정보가 존재하지 않습니다."));

        Court court = courtRepository.findById(createMatchDto.getCourtId())
            .orElseThrow(() -> new IllegalArgumentException("코트 정보가 존재하지 않습니다."));

        boolean isDuple = matchRepository.existsByMatchDateAndMatchTimeAndCourt(createMatchDto.getMatchDate(),
            createMatchDto.getMatchTime(), court);

        if (!isDuple) {
            Match match = createMatchDto.toEntity(createMatchDto, court, user.getName());
            matchRepository.save(match);
        } else {
            throw new IllegalArgumentException("이미 등록된 매치가 있습니다.");
        }

    }

}
