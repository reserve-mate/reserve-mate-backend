package com.reservemate.reserve_mate_backend.admin.match.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.PlayerEjectRequest;
import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.validator.MatchValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMatchPlayerService {

    private final MatchPlayerRepository matchPlayerRepository;
    private final FacilityManagerRepository facilityManagerRepository;
    private final CourtRepository courtRepository;
    private final MatchValidator matchValidator;
    private final JwtUtil jwtUtil;

    /* 대시보드 매치 총 이용자 수 */
    public Integer getAdminMatchPlayerCount(Long userId) {
        List<FacilityManager> managers = facilityManagerRepository.findByUserId(userId);
        List<Long> facilityIds = FacilityManager.getFacilityIds(managers);
        List<Court> courts = courtRepository.findByFacilityIds(facilityIds);

        List<Match> matches = matchValidator.getCourtsMatches(courts);   // 매치 리스트
        List<PlayerStatus> playerStatus = List.of(PlayerStatus.COMPLETED, PlayerStatus.ONGOING, PlayerStatus.READY,
            PlayerStatus.KICKED);
        int matchPlayerCnt = matchPlayerRepository.countByMatchInAndStatusIn(matches, playerStatus);

        return matchPlayerCnt;
    }

    /* 매치 퇴장 */
    @Transactional
    public void removePlayerFromMatch(HttpServletRequest request, Long playerId, PlayerEjectRequest ejectRequest) {

        String accessToken = request.getHeader("access");
        Long userId = jwtUtil.getId(accessToken);

        FacilityManager facilityManager = facilityManagerRepository.findByUserIdAndfacilityId(userId, ejectRequest
            .getFacilityId())
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        MatchPlayer matchPlayer = matchPlayerRepository.findById(playerId)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PLAYER));
        matchPlayer.isNotOngoingPlayer();

        Match match = matchPlayer.getMatch();
        match.isNotOngoinChk();
        match.validateManager(facilityManager.getId());

        matchPlayer.removePlayer(ejectRequest.getEjectionReason());
    }

}
