package com.reservemate.reserve_mate_backend.admin.match.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.AdminMatchModifyRequest;
import com.reservemate.reserve_mate_backend.admin.match.dto.request.AdminMatchesRequest;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchDetailResponse;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchesResponse;
import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.dto.request.PlayerOngingRequest;
import com.reservemate.reserve_mate_backend.match.repository.MatchCustomRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.dto.request.MatchCancelPaymentRequest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMatchService {

    private final MatchCustomRepository matchCustomRepository;
    private final MatchRepository matchRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final CourtRepository courtRepository;
    private final FacilityManagerRepository facilityManagerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtUtil jwtUtil;

    /* 매치 정보 수정 */
    @Transactional
    public void adminMatchModify(Long matchId, AdminMatchModifyRequest modifyRequest) {

        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));
        match.isModifiable();

        int playerCnt = matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY);
        modifyRequest.isTeamCapacityOver(playerCnt);
        match.chgModifyMatchStat(playerCnt, modifyRequest.getTeamCapacity());

        Court court = courtRepository.findById(modifyRequest.getFacilityCourtId())
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_INPUT_VALUE));

        FacilityManager manager = facilityManagerRepository.findById(modifyRequest.getManagerId())
            .orElseThrow(() -> new ApiException(ErrorCode.INVALID_INPUT_VALUE));

        // 해당 시간대에 겹치는 코트가 있는지 검증
        List<MatchStatus> matchStatus = List.of(MatchStatus.CANCELLED, MatchStatus.END);
        List<Match> matches = matchRepository.findByMatchDateAndCourtAndMatchStatusNotInAndMatchIdNot(modifyRequest
            .getMatchDate(), court, matchStatus, matchId);
        Match.isTimeConfilict(matches, modifyRequest.getMatchTime(), modifyRequest.getEndTime());

        // 해당 매니저가 다른 매치에도 배정되어있는지 검증
        boolean isDupleMatchManager = matchRepository.existsConflictManagerAnotherMatch(modifyRequest.getMatchDate(),
            manager.getId(), court.getId(), modifyRequest.getMatchTime(), modifyRequest.getEndTime(), matchId);

        if (isDupleMatchManager) {
            throw new ApiException(ErrorCode.MANAGER_ALREADY_ASSIGNED);
        }

        match.matchModify(modifyRequest, court, manager);

        // 알림 기능 구현??
    }

    /* 매치 상태 변경 */
    @Transactional
    public void matchStatusChange(Long matchId, MatchStatus matchStatus) {

        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));
        match.isAvailableStatChg();

        if (matchStatus == MatchStatus.END) {
            match.isEndMatch();
            match.isNotOngoinChk();
        } else if (matchStatus == MatchStatus.ONGOING || matchStatus == MatchStatus.FINISH) {
            List<MatchPlayer> matchPlayers = matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY);
            int playerCnt = matchPlayers.size() + 1;

            if (matchStatus == MatchStatus.ONGOING) {
                match.isOngoinChk();
                match.isNotFinishOrClose(playerCnt);
                match.validateOngoingTransitionByTime();

                eventPublisher.publishEvent(new PlayerOngingRequest(matchPlayers));
            } else if (matchStatus == MatchStatus.FINISH) {
                match.isFinish();
                match.isNotCloseToDeadLine(playerCnt);
            }
        } else if (matchStatus == MatchStatus.CLOSE_TO_DEADLINE) {
            match.isNotFinish();
        }

        match.matchStatusChange(matchStatus);
    }

    // 관리자 매치 삭제
    @Transactional
    public void deleteMatch(Long matchId) {

        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));
        match.isDeletable();
        List<MatchPlayer> matchPlayers = matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY);

        if (!matchPlayers.isEmpty()) {
            eventPublisher.publishEvent(new MatchCancelPaymentRequest(matchPlayers));
        }

        match.matchCancel();
    }

    /* 관리자 매치 상세 */
    public AdminMatchDetailResponse getAdminMatchDetail(Long matchId) {
        Match match = matchRepository.findById(matchId).orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));

        List<PlayerStatus> playerStatus = List.of(PlayerStatus.KICKED, PlayerStatus.READY, PlayerStatus.ONGOING,
            PlayerStatus.COMPLETED);
        List<MatchPlayer> players = matchPlayerRepository.findByMatchAndStatusIn(match, playerStatus);

        return AdminMatchDetailResponse.getAdminMatchDetailResponse(match, players);
    }

    /* 관리자 매치 목록 조회 */
    @Transactional
    public Slice<AdminMatchesResponse> getMatches(HttpServletRequest request, AdminMatchesRequest adminMatchesRequest) {
        adminMatchesRequest.initSportType();

        String accessToken = request.getHeader("access");
        if (accessToken == null) {
            throw new ApiException(ErrorCode.ADMIN_FORBIDDEN);
        }
        Long userId = jwtUtil.getId(accessToken);

        Pageable pageable = PageRequest.of(adminMatchesRequest.getPageNumber(), 6);

        Slice<AdminMatchesResponse> matchesResponses = matchCustomRepository.getAdminMatches(userId,
            adminMatchesRequest, pageable);

        return matchesResponses;
    }

}
