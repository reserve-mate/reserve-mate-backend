package com.reservemate.reserve_mate_backend.match.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.match.domain.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {

    boolean existsByMatchDateAndMatchTimeAndCourt(LocalDate matchDate, int matchTime, Court court);

    List<Match> findByMatchDateAndCourt(LocalDate matchDate, Court court);

}
