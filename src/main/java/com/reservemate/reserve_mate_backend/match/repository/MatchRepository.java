package com.reservemate.reserve_mate_backend.match.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.match.domain.Match;

public interface MatchRepository extends JpaRepository<Match, Long>{

}
