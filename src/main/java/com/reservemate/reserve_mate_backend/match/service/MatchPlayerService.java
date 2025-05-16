package com.reservemate.reserve_mate_backend.match.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.dto.request.ApplyPlayerDto;
import com.reservemate.reserve_mate_backend.match.dto.request.CancelMatchRequest;
import com.reservemate.reserve_mate_backend.match.dto.request.CancelPlayerDto;
import com.reservemate.reserve_mate_backend.match.dto.request.RequestMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchApplyResponse;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.dto.request.CancelPaymentDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.RequestPaymentDto;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MatchPlayerService {

    @Value("${toss.pay.successurl}")
    private String successUrl;

    @Value("${toss.pay.failurl}")
    private String failUrl;

    private final MatchPlayerRepository matchPlayerRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final JwtUtil jwtUtil;

    /* 매치 플레이어 진행중으로 상태 변경 */
    @Transactional
    public void changePlayerOngoing(List<MatchPlayer> matchPlayers) {
        List<Long> playerIds = matchPlayers.stream()
            .map((player) -> player.getPlayerId()).toList();

        // 매치 플레이어 상태값 변경
        matchPlayerRepository.updateOngoinPlayer(playerIds);
    }

    /* 매치 취소 */
    @Transactional
    public String cancelMatch(HttpServletRequest request, CancelMatchRequest cancelMatchRequest) {

        String accessToken = request.getHeader("access");
        if (accessToken == null) {
            throw new ApiException(ErrorCode.UNAUTHORIZED_CODE);
        }

        Long userId = jwtUtil.getId(accessToken);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Match match = matchRepository.findById(cancelMatchRequest.getMatchId())
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));
        match.isEndMatch();

        MatchPlayer matchPlayer = matchPlayerRepository.findByUserAndMatchAndStatus(user, match, PlayerStatus.READY)
            .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND_PLAYER));
        matchPlayer.isCanCancel();

        eventPublisher.publishEvent(new CancelPaymentDto(cancelMatchRequest.getOrderId(), cancelMatchRequest
            .getCancelReason()));

        matchPlayer.chgStatusCancel();

        int playerCnt = matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY);
        match.chgMatchStatus(playerCnt);

        return cancelMatchRequest.getOrderId();
    }

    /* 매치 신청 요청 */
    @Transactional
    public MatchApplyResponse requestApplyMatch(HttpServletRequest request, RequestMatchDto requestMatchDto) {

        String accessToken = request.getHeader("access");
        Long userId = jwtUtil.getId(accessToken);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Match match = matchRepository.findById(requestMatchDto.getMatchId())
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));
        match.isEndMatch();
        match.isFinish();
        match.validatePrice(requestMatchDto.getAmount());

        boolean existPlayer = matchPlayerRepository.existsByUserAndMatchAndStatusNot(user, match, PlayerStatus.CANCEL);
        if (existPlayer)
            throw new ApiException(ErrorCode.EXIST_MATCH_PLAYER_ERROR);

        /* 결제 요청(DB 저장) */
        eventPublisher.publishEvent(RequestPaymentDto.toRequestPaymentDto(requestMatchDto, user, match));

        return MatchApplyResponse.toMatchApplyResponse(requestMatchDto, user, match.getMatchName(), successUrl,
            failUrl);
    }

    /*
     * 신청해도 되는 매치인지 검증
     */
    @Transactional
    public boolean verifyApplyMatch(HttpServletRequest request, Long matchId, int amount) {
        String accessToken = request.getHeader("access");
        Long userId = jwtUtil.getId(accessToken);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));
        match.isEndMatch();
        match.isFinish();
        match.validatePrice(amount);

        return matchPlayerRepository.existsByUserAndMatchAndStatusNot(user, match, PlayerStatus.CANCEL);
    }

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
