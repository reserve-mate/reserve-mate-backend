package com.reservemate.reserve_mate_backend.match.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;

import jakarta.persistence.LockModeType;

public interface MatchRepository extends JpaRepository<Match, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)   // jpa 비관적 락
    @Query("select m from Match m where m.matchId = :matchId")
    Optional<Match> findByIdWithLock(@Param("matchId") Long matchId);

    boolean existsByMatchDateAndMatchTimeAndCourt(LocalDate matchDate, int matchTime, Court court);

    List<Match> findByMatchDateAndCourt(LocalDate matchDate, Court court);

    List<Match> findByMatchDateAndMatchTimeAndMatchStatusNot(LocalDate matchDate, int nowTime, MatchStatus end);

    @Modifying(clearAutomatically = true)
    @Query("update Match m set m.matchStatus = :end, m.updatedAt = now() where m.matchDate = :matchDate and m.matchTime = :nowTime and m.matchStatus <> :end")
    void updateEndBeforeMatch(@Param("matchDate") LocalDate matchDate, @Param("nowTime") int nowTime,
        @Param("end") MatchStatus end);

    @Query("select m.matchId from Match m where m.matchDate = :matchDate and m.matchTime = :matchTime")
    List<Long> findByMatchDateAndMatchTimeLong(@Param("matchDate") LocalDate matchDate,
        @Param("matchTime") int matchTime);

    List<Match> findByMatchDateAndMatchTime(LocalDate matchDate, int matchTime);

    @Modifying(clearAutomatically = true)
    @Query("update Match m set m.matchStatus = 'END', m.updatedAt = now() where m.matchId in (:matchIds)")
    void updateBeforeMatchs(@Param("matchIds") List<Long> matchIds);

    // 해당 매니저가 다른 매치와 시간이 겹치는지 검증(해당 매치 제외)
    @Query("select case when count(m) > 0 then true else false end"
        + " from Match m"
        + " where m.matchDate = :matchDate"
        + " and m.facilityManager.id = :managerId"
        + " and m.court.id != :courtId"
        + " and m.matchTime < :matchEndTime"
        + " and m.endTime > :matchTime")
    boolean existsConflictManager(@Param("matchDate") LocalDate matchDate, @Param("managerId") Long facilityManager,
        @Param("courtId") Long court, @Param("matchTime") Integer matchTime,
        @Param("matchEndTime") Integer matchEndTime);

    // 금일 현재 시간 매치 조회(MatchStatus.APPLICABLE, MatchStatus.CLOSE_TO_DEADLINE, MatchStatus.FINISH)
    List<Match> findByMatchDateAndMatchTimeAndMatchStatusIn(LocalDate matchDate, int matchTime,
        List<MatchStatus> status);

    // 금일 현재 시간 진행중 매치 조회(ONGOING)
    List<Match> findByMatchDateAndEndTimeAndMatchStatus(LocalDate now, int i, MatchStatus ongoing);

    // 취소되거나 종료된 매치를 제외한 매치 조회
    List<Match> findByMatchDateAndCourtAndMatchStatusNotIn(LocalDate matchDate, Court court,
        List<MatchStatus> matchStatus);

    // 취소되거나 종료된 매치를 제외한 매치 조회(본인 매치 제외)
    List<Match> findByMatchDateAndCourtAndMatchStatusNotInAndMatchIdNot(LocalDate matchDate, Court court,
        List<MatchStatus> matchStatus, Long matchId);

    // 해당 매니저가 다른 매치와 시간이 겹치는지 검증(해당 매치 제외)
    @Query("select case when count(m) > 0 then true else false end"
        + " from Match m"
        + " where m.matchDate = :matchDate"
        + " and m.facilityManager.id = :managerId"
        + " and m.court.id != :courtId"
        + " and m.matchTime < :matchEndTime"
        + " and m.endTime > :matchTime"
        + " and m.matchId != :matchId")
    boolean existsConflictManagerAnotherMatch(@Param("matchDate") LocalDate matchDate,
        @Param("managerId") Long facilityManager, @Param("courtId") Long court, @Param("matchTime") Integer matchTime,
        @Param("matchEndTime") Integer endTime, @Param("matchId") Long matchId);

    @Query("select case when count(m) > 0 then true else false end"
        + " from Match m"
        + " where m.matchDate = :matchDate"
        + " and m.court.id = :courtId"
        + " and m.matchTime < :endTime"
        + " and m.endTime > :startTime"
        + " and m.matchStatus not in (:matchStatus)"
    )
    boolean existsMatchReaserveDate(@Param("matchDate") LocalDate reserveDate, @Param("startTime") int startTime,
        @Param("endTime") int endTime, @Param("matchStatus") List<MatchStatus> matchStatus,
        @Param("courtId") Long courtId);

    @Query("select m from Match m where m.court.id in (:courtIds) and m.matchDate between :startDate and :endDate")
    List<Match> findByCourtIdsMatchDate(@Param("courtIds") List<Long> courtIds, @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);

    @Query("select m from Match m where m.court.id in (:courtIds) and m.matchDate between :startDate and :endDate and m.court.facility.id = :facilityId")
    List<Match> findByCourtIdsMatchDateFacility(@Param("courtIds") List<Long> courtIds,
        @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
        @Param("facilityId") Long facilityId);

}
