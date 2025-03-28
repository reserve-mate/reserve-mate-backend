package com.reservemate.reserve_mate_backend.match.repository;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class MatchRepositoryTest {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    private Court court;

    @BeforeEach
    void setup() {
        Facility facility = Facility.builder()
            .name("시설")
            .build();

        Facility saveFacility = facilityRepository.save(facility);

        court = Court.builder()
            .name("운동 코트")
            .sportType(SportType.FUTSAL)
            .capacity(12)
            .indoor(false)
            .facility(saveFacility)
            .build();
    }

    @Test
    @DisplayName("매치 중복 검사")
    void dupleMatchTest() {
        /* given */
        Court saveCourt = courtRepository.save(court);
        LocalDate date = LocalDate.of(2025, 3, 26);
        int matchTime = 16;

        /* when */
        boolean isDuple = matchRepository.existsByMatchDateAndMatchTimeAndCourt(date, matchTime, saveCourt);

        Assertions.assertThat(isDuple).isFalse();
    }

    /* 매치 기본 설정 */
    private Match getMatch(Court court) {
        //Court court = courtRepository.findById(1)
        Match match = Match.builder()
            .manager("manager")
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(18)
            .matchDate(LocalDate.now())
            .matchTime(10)
            .matchPrice(11000)
            .court(court)
            .build();
        return match;
    }
}
