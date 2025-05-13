package com.reservemate.reserve_mate_backend.match.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.user.domain.User;

public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {

    int countByMatch(Match match);

    boolean existsByUserAndMatch(User user, Match match);

    Optional<MatchPlayer> findByUserAndMatchAndStatus(User user, Match match, PlayerStatus status);

    boolean existsByUserAndMatchAndStatusNot(User user, Match match, PlayerStatus cancel);

    int countByMatchAndStatus(Match match, PlayerStatus ready);

    List<MatchPlayer> findByMatchAndStatus(Match match, PlayerStatus ready);

    @Modifying(clearAutomatically = true)
    @Query("update MatchPlayer mp set mp.status = :playerStatus where mp.match.matchId = :matchId")
    void updatePlayersMatchRemoved(@Param("matchId") Long matchId, @Param("playerStatus") PlayerStatus playerStatus);

    @Modifying(clearAutomatically = true)
    @Query("update MatchPlayer mp set mp.status = 'COMPLETED', mp.updatedAt = now() where mp.match.matchId in (:matchIds)")
    void updateBeforeMatchs(@Param("matchIds") List<Long> matchIds);

    // 해당 매치와 관련된 유저의 신청 정보 조회
    List<MatchPlayer> findByMatchAndUser(Match match, User user);

}
