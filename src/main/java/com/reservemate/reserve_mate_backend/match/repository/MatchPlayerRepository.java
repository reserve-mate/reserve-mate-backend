package com.reservemate.reserve_mate_backend.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.user.domain.User;

public interface MatchPlayerRepository extends JpaRepository<MatchPlayer, Long> {

    int countByMatch(Match match);

    boolean existsByUserAndMatch(User user, Match match);

    MatchPlayer findByUserAndMatch(User user, Match match);

    boolean existsByUserAndMatchAndStatusNot(User user, Match match, PlayerStatus cancel);

}
