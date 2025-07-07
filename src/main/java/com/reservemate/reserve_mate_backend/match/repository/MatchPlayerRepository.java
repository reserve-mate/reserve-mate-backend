package com.reservemate.reserve_mate_backend.match.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
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

    /* 해당 매치의 참가자 조회 */
    @EntityGraph(attributePaths = {"user"})
    List<MatchPlayer> findByMatchAndStatus(Match match, PlayerStatus ready);

    @Modifying(clearAutomatically = true)
    @Query("update MatchPlayer mp set mp.status = :playerStatus where mp.match.matchId = :matchId")
    void updatePlayersMatchRemoved(@Param("matchId") Long matchId, @Param("playerStatus") PlayerStatus playerStatus);

    @Modifying(clearAutomatically = true)
    @Query("update MatchPlayer mp set mp.status = 'COMPLETED', mp.updatedAt = now() where mp.match.matchId in (:matchIds)")
    void updateBeforeMatchs(@Param("matchIds") List<Long> matchIds);

    // 해당 매치와 관련된 유저의 신청 정보 조회
    List<MatchPlayer> findByMatchAndUser(Match match, User user);

    // 매치에 참가자가 있는지 검증
    boolean existsByMatchAndStatus(Match match, PlayerStatus ready);

    // 매치 플레이어 진행중으로 상태 변경
    @Modifying(clearAutomatically = true)
    @Query("update MatchPlayer mp set mp.status = :playerStatus where mp.playerId in (:playerIds)")
    void updateOngoinPlayer(@Param("playerIds") List<Long> playerIds, @Param("playerStatus") PlayerStatus playerStatus);

    // 매치 플레이어 참가자 및 퇴장자 조회
    List<MatchPlayer> findByMatchAndStatusIn(Match match, List<PlayerStatus> playerStatus);

    // 유저 조회
    List<MatchPlayer> findByUser(User user);

    // 유저 조회 페이징
    Slice<MatchPlayer> findByUser(User user, Pageable pageable);

    // 유저 조회 상태(단일 상태)
    Slice<MatchPlayer> findByUserAndStatus(User user, PlayerStatus playerStatus, Pageable pageable);

    // 유저 조회 상태(여러 상태)
    Slice<MatchPlayer> findByUserAndStatusIn(User user, List<PlayerStatus> playerStatus, Pageable pageable);

    // 매치 플레이어 참가자 및 퇴장자 조회(여러 매치)
    int countByMatchAndStatusIn(Match match, List<PlayerStatus> playerStatus);

    // 매치 플레이어 참가자 및 퇴장자 조회(여러 매치)
    int countByMatchInAndStatusIn(List<Match> matches, List<PlayerStatus> playerStatus);

    /* 해당 매치의 유저 상태 검사 */
    @Query("select mp from MatchPlayer mp where mp.match.matchId = :matchId and mp.user.id = :userId and mp.status = :status")
    Optional<MatchPlayer> findByMatchIdAndUserIdAndStatus(@Param("matchId") Long matchId, @Param("userId") Long userId,
        @Param("status") PlayerStatus completed);

}
