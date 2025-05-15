package com.reservemate.reserve_mate_backend.admin.match.service;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.AdminMatchesRequest;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchDetailResponse;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchesResponse;
import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
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
    private final ApplicationEventPublisher eventPublisher;
    private final JwtUtil jwtUtil;

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
            int playerCnt = matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY) + 1;

            if (matchStatus == MatchStatus.ONGOING) {
                match.isOngoinChk();
                match.isNotFinishOrClose(playerCnt);
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

        List<MatchPlayer> players = matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY);

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
