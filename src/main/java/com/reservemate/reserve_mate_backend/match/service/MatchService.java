package com.reservemate.reserve_mate_backend.match.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityImage;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityImageRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.dto.request.MatchSearchDto;
import com.reservemate.reserve_mate_backend.match.dto.request.ModifyMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.request.PlayerOngingRequest;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDateDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDetailDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchHistroyResponse;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchesDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchCustomRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.dto.request.MatchCancelPaymentRequest;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
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
    private final UserRepository userRepository;
    private final MatchPlayerRepository matchPlayerRepository;
    private final FacilityImageRepository facilityImageRepository;
    private final MatchCustomRepository matchCustomRepository;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    private final AmazonS3 amazonS3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    // /* 시간이 지난 날짜 종료 처리 */
    @Transactional
    public void endBeforeMatch() {
        log.info("is transaction active: {}", TransactionSynchronizationManager.isActualTransactionActive());
        matchTimeOngoing(); // 현재 시간이 매치 시간에 도달한 경우
        expireIfEndTimeReached(); // 현재 시간이 매치 종료 시간에 도달한 경우
    }

    // 현재 시간이 매치 종료 시간에 도달한 경우
    private void expireIfEndTimeReached() {
        int batchSize = 1000;

        List<Match> matches = matchRepository.findByMatchDateAndEndTimeAndMatchStatus(LocalDate.now(), Utils
            .getNowTime(),
            MatchStatus.ONGOING);

        if (!matches.isEmpty()) {
            for (int i = 0; i < matches.size(); i += batchSize) {
                List<Match> goingMatches = matches.subList(i, Math.min(i + batchSize, matches.size()));
                endTimeBatchProcess(goingMatches);
            }
        }

    }

    // 진행중인 매치 종료 시간되면 종료 상태로 change
    private void endTimeBatchProcess(List<Match> goingMatches) {
        for (Match match : goingMatches) {
            List<MatchPlayer> matchPlayers = matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.ONGOING);
            match.chgEndMatch();
            eventPublisher.publishEvent(new PlayerOngingRequest(matchPlayers, PlayerStatus.COMPLETED));
            matchRepository.save(match);
        }
    }

    // 시간별 상태 변경 배치 처리
    private void matchTimeOngoing() {

        int batchSize = 1000;

        List<MatchStatus> status = List.of(MatchStatus.APPLICABLE, MatchStatus.CLOSE_TO_DEADLINE, MatchStatus.FINISH);

        List<Match> matches = matchRepository.findByMatchDateAndMatchTimeAndMatchStatusIn(LocalDate.now(),
            (Utils.getNowTime()), status);

        if (!matches.isEmpty()) {
            for (int i = 0; i < matches.size(); i += batchSize) {
                List<Match> batchMatch = matches.subList(i, Math.min(i + batchSize, matches.size()));
                processBatch(batchMatch);
            }
        }
    }

    // 매치 진행 중 or 종료 처리
    private void processBatch(List<Match> batchMatch) {
        for (Match match : batchMatch) {
            List<MatchPlayer> matchPlayers = matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY);
            MatchStatus matchStatus = match.getMatchStatus();
            if (matchStatus == MatchStatus.APPLICABLE || matchStatus == MatchStatus.CLOSE_TO_DEADLINE) {

                if (matchStatus == MatchStatus.CLOSE_TO_DEADLINE) {
                    match.matchStatChangeSchedule(matchPlayers.size());
                } else if (matchStatus == MatchStatus.APPLICABLE) {
                    match.chgEndMatch();
                }

                if (match.getMatchStatus() == MatchStatus.END) { // 매치 종료 될 시 환불 처리
                    eventPublisher.publishEvent(new MatchCancelPaymentRequest(matchPlayers));
                } else if (match.getMatchStatus() == MatchStatus.ONGOING) {   // 매치 진행 중 처리 시 참가자 상태 변경
                    eventPublisher.publishEvent(new PlayerOngingRequest(matchPlayers, PlayerStatus.ONGOING));
                }
            } else if (matchStatus == MatchStatus.FINISH) {
                log.info("Before status: {}", match.getMatchStatus());
                match.chgMatchOngoin();
                log.info("After status: {}", match.getMatchStatus());
                eventPublisher.publishEvent(new PlayerOngingRequest(matchPlayers, PlayerStatus.ONGOING));
            }

            matchRepository.save(match);
        }
    }

    // 매치 이용 내역
    public Slice<MatchHistroyResponse> getMatchHistory(Long userId, String matchStatus, int pageNum) {

        Pageable pageable = PageRequest.of(pageNum, 6);

        List<String> tabs = List.of("all", "upcoming", "completed", "canceled");
        if (!tabs.contains(matchStatus)) {
            throw new ApiException(ErrorCode.MISSING_QUERY_PARAM);
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        Slice<MatchPlayer> matchPlayers = matchPlayerRepository.findByUser(user, pageable);
        if (matchStatus.equals("upcoming")) {
            matchPlayers = matchPlayerRepository.findByUserAndStatus(user, PlayerStatus.READY, pageable);
        } else if (matchStatus.equals("completed")) {
            List<PlayerStatus> playerStatus = List.of(PlayerStatus.COMPLETED, PlayerStatus.ONGOING,
                PlayerStatus.KICKED);
            matchPlayers = matchPlayerRepository.findByUserAndStatusIn(user, playerStatus, pageable);
        } else if (matchStatus.equals("canceled")) {
            List<PlayerStatus> playerStatus = List.of(PlayerStatus.CANCEL, PlayerStatus.MATCH_CANCELLED);
            matchPlayers = matchPlayerRepository.findByUserAndStatusIn(user, playerStatus, pageable);
        }

        List<MatchHistroyResponse> content = matchPlayers.stream()
            .map(matchPlayer -> {
                int playerCnt = getPlayerCnt(matchPlayer.getMatch());
                return MatchHistroyResponse.getMatchHistroyResponse(matchPlayer, playerCnt);
            }).toList();

        Slice<MatchHistroyResponse> sliceResponse = new SliceImpl<>(content, pageable, matchPlayers.hasNext());

        return sliceResponse;
    }

    // 매치 플레이어 카운트
    private int getPlayerCnt(Match match) {

        List<MatchStatus> beforeStatus = List.of(MatchStatus.APPLICABLE, MatchStatus.CLOSE_TO_DEADLINE,
            MatchStatus.FINISH);
        List<MatchStatus> afterStatus = List.of(MatchStatus.END, MatchStatus.CANCELLED);

        List<PlayerStatus> playerStatus = null;
        if (beforeStatus.contains(match.getMatchStatus())) {
            playerStatus = List.of(PlayerStatus.READY);
        } else if (match.getMatchStatus() == MatchStatus.ONGOING) {
            playerStatus = List.of(PlayerStatus.ONGOING, PlayerStatus.KICKED);
        } else if (afterStatus.contains(match.getMatchStatus())) {
            playerStatus = List.of(PlayerStatus.COMPLETED, PlayerStatus.KICKED);
        }
        return matchPlayerRepository.countByMatchAndStatusIn(match, playerStatus);
    }

    // s3 파일 업로드 테스트
    public String uploadFile(MultipartFile multipartFile) throws IOException {
        String filename = UUID.randomUUID().toString();

        // 메타 데이터 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(multipartFile.getContentType());
        metadata.setContentLength(multipartFile.getSize());

        // S3에 파일 업로드 요청 생성
        PutObjectRequest objectRequest = new PutObjectRequest(bucket, filename, multipartFile.getInputStream(),
            metadata);

        // S3에 파일 업로드
        amazonS3.putObject(objectRequest);

        return Utils.getPublicUrl(bucket, amazonS3.getRegionName(), filename);
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
            matchPlayerRepository.updatePlayersMatchRemoved(matchId, PlayerStatus.MATCH_CANCELLED);
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
        matchSearchDto.initMatchDateIfNull();

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
    public MatchDetailDto getMatch(Long userId, Long matchId) {

        User user = null;
        Payment payment = null;

        if (userId != null) {
            user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

            payment = paymentRepository.findByMatchIdAndUserId(matchId, userId).orElse(payment);
        }

        Match match = matchRepository.findById(matchId)
            .orElseThrow(() -> new ApiException(ErrorCode.NO_MATCH_ERROR));

        List<MatchPlayer> matchPlayers = matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY);

        List<FacilityImage> images = facilityImageRepository.findByFacility(match.getFacility());

        return MatchDetailDto.toMatchDetailDto(match, user, matchPlayers, images, payment);
    }

}
