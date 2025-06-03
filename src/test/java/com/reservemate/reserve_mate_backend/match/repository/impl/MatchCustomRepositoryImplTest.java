package com.reservemate.reserve_mate_backend.match.repository.impl;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MatchCustomRepositoryImplTest {

    private Court court;

    @BeforeEach
    void setup() {
        court = Court.builder()
            .name("운동 코트")
            //.sportType(SportType.FUTSAL)
            //.capacity(12)
            .indoor(false)
            .fee(0)
            .build();

        court.activate();
    }

    @Test
    @DisplayName("매치 목록 조회")
    void testGetMatches() {

    }

    private Match getMatch() {
        Match match = Match.builder()
            //.manager("manager")
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(18)
            .matchDate(LocalDate.now())
            .matchTime(10)
            .matchPrice(11000)
            .court(court)
            .build();
        return null;
    }
}
