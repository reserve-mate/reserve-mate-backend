package com.reservemate.reserve_mate_backend.admin.match.service;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.PlayerEjectRequest;
import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminMatchPlayerService {

    private final MatchPlayerRepository matchPlayerRepository;
    private final FacilityManagerRepository facilityManagerRepository;
    private final JwtUtil jwtUtil;

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
