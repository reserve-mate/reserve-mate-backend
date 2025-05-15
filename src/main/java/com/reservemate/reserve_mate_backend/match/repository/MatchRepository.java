package com.reservemate.reserve_mate_backend.match.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;

public interface MatchRepository extends JpaRepository<Match, Long> {

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

}
