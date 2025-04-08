package com.reservemate.reserve_mate_backend.match.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityImage;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityImageRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.dto.request.CreateMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.request.MatchSearchDto;
import com.reservemate.reserve_mate_backend.match.dto.request.ModifyMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDateDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDetailDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchesDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchCustomRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class MatchService {

    private final MatchRepository matchRepository;
    private final CourtRepository courtRepository;
    private final UserRepository userRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final FacilityImageRepository facilityImageRepository;
    private final MatchCustomRepository matchCustomRepository;
    private final FacilityManagerRepository facilityManagerRepository;

    /* 시간이 지난 날짜 종료 처리 */
    @Scheduled(cron = "0 0 6-23 * * *") // 5초마다 실행
    @Transactional
    public void endBeforeMatch() {
        log.info("---------" + LocalTime.now().getHour() + "시 ---------");
        matchRepository.updateEndBeforeMatch(LocalDate.now(), (Utils.getNowTime()), MatchStatus.END);
    }

    @Transactional
    public void deleteMatch(Long matchId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.isAdmin(); // 관리자 권한인지 검사

        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));

        List<MatchPlayer> matchPlayers = matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY);

        if (!matchPlayers.isEmpty()) {
            matchPlayerRepository.updatePlayersMatchRemoved(matchId, PlayerStatus.MATCH_REMOVED);
        }

        // 환불 로직

        matchRepository.delete(match);
    }

    @Transactional
    public void reReCruit(Long matchId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.isAdmin(); // 관리자 권한인지 검사

        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));
        match.isNotFinish(); // 마감된 매치인지 검사

        int playerCnt = matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY);
        match.reCruit(playerCnt);
    }

    @Transactional
    public void chgEndMatch(Long matchId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        user.isAdmin();

        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));
        match.isFinish();

        match.chgFinish();
    }

    /* 매치 조회(일반 사용자) */
    @Transactional
    public Slice<MatchesDto> getMatches(MatchSearchDto matchSearchDto) {
        matchSearchDto.setMatchDate();

        Pageable pageable = PageRequest.of(matchSearchDto.getPageNumber(), 6);
        Slice<MatchesDto> matches = matchCustomRepository.getMatches(pageable, matchSearchDto);

        return matches;
    }

    /*
     * 날짜별 매치 조회
     */
    @Transactional
    public List<MatchDateDto> getMatchDates(MatchSearchDto matchSearchDto) {

        List<MatchDateDto> dateDtos = matchCustomRepository.getMatchesForDate(matchSearchDto);

        return MatchDateDto.getMatchMonthDates(matchSearchDto.getMatchDate(), dateDtos);
    }

    /*
     * 매치 정보 수정
     */
    @Transactional
    public void modifyMatch(Long matchId, ModifyMatchDto modifyMatchDto) {
        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));

        match.isEndMatch(); // 종료된 매치인지 검사

        int playerCnt = matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY);

        modifyMatchDto.isOverTeamCapacity(playerCnt);   // 준비된 인원 수 초과 검사

        match.modifyMatch(modifyMatchDto.getTeamCapacity(), modifyMatchDto.getDescription(), modifyMatchDto
            .getMatchName());

    }

    /*
     * 매치 상세
     */
    public MatchDetailDto getMatch(Long matchId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));

        List<MatchPlayer> matchPlayers = matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY);

        List<FacilityImage> images = facilityImageRepository.findByFacility(match.getFacility());

        return MatchDetailDto.toMatchDetailDto(match, user, matchPlayers, images);
    }

    /*
     * 매치 등록
     */
    @Transactional
    public void registMatch(CreateMatchDto createMatchDto) {

        Court court = courtRepository.findById(createMatchDto.getCourtId())
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_INPUT_VALUE));

        List<Match> matches = matchRepository.findByMatchDateAndCourt(createMatchDto.getMatchDate(), court);
        Match.isTimeConfilict(matches, createMatchDto.getMatchTime(), createMatchDto.getMatchEndTime());

        FacilityManager facilityManager = facilityManagerRepository.findById(createMatchDto.getManagerId())
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_INPUT_VALUE));

        boolean isExistMatch = matchRepository
            .existsByMatchDateAndMatchTimeAndCourt(createMatchDto.getMatchDate(), createMatchDto.getMatchTime(), court);

        if (isExistMatch)
            throw new ApiException(ErrorCode.EXIST_MATCH_ERROR);

        Match match = createMatchDto.toEntity(createMatchDto, court, facilityManager);
        matchRepository.save(match);
    }

}
