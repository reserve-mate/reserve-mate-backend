package com.reservemate.reserve_mate_backend.match.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.user.domain.User;

public class MatchTest {

    private User user;
    private Facility facility;
    private Court court;
    private Match match;
    private FacilityManager facilityManager;

    @BeforeEach
    void setUp() {
        user = getUser();
        facility = getFacility();
        court = getCourt(facility);
        facilityManager = getFacilityManager(user, facility);
        match = getMatch(court, facilityManager);
    }

    @Test
    @DisplayName("시간이 겹치는 매치인지 검증")
    void testIsTimeConfilict() {
        /* given */
        // 예시: 기존 매치들 (16:00 ~ 18:00, 19:00 ~ 21:00)

        Court court1 = new Court(2L, "운동 코트", CourtType.ARTIFICIAL_TURF_FUTSAL, 20, 40, false, 0, facility);
        List<Match> existingMatches = List.of(
            new Match(16, 18, facilityManager, court),
            new Match(20, 22, facilityManager, court1)
        );

        int startTime = 16;
        int endTime = 18;

        /* then */
        assertThatThrownBy(() -> Match.isTimeConfilict(existingMatches, startTime, endTime))
            .isInstanceOf(ApiException.class)
            .hasMessage("겹치는 시간대에 매치가 존재합니다.");
    }

    @Test
    @DisplayName("날짜가 지난 매치인지 검증")
    void testIsOverMatch() {
        /* given */
        Match pastMatch = Match.builder()
            .matchId(1L)
            .matchName("매치")
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(18)
            .matchDate(LocalDate.now().minusDays(1))
            .matchTime(12)
            .endTime(14)
            .matchPrice(11000)
            .court(court)
            .facilityManager(facilityManager)
            .build();

        /* then */
        assertThatThrownBy(() -> pastMatch.isOverMatch())
            .isInstanceOf(ApiException.class)
            .hasMessage("이미 진행중 또는는 종료된 매치입니다.");
    }

    private Match getMatch(Court court, FacilityManager facilityManager) {
        Match match = Match.builder()
            .matchId(1L)
            .matchName("매치")
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(18)
            .matchDate(LocalDate.now())
            .matchTime(12)
            .endTime(14)
            .matchPrice(11000)
            .court(court)
            .facilityManager(facilityManager)
            .build();
        return match;
    }

    // 매니저 데이터 저장
    private FacilityManager getFacilityManager(User user, Facility facility) {
        FacilityManager manager = new FacilityManager(1L, facility, user);
        return manager;
    }

    private Facility getFacility() {
        Address address = Address.builder()
            .city("서울")
            .build();

        Facility facility = Facility.builder()
            .name("시설")
            .address(address)
            .build();
        return facility;
    }

    private Court getCourt(Facility facility) {

        Court court = new Court(1L, "운동 코트", CourtType.ARTIFICIAL_TURF_FUTSAL, 20, 40, false, 0, facility);

        return court;
    }

    /* 회원 기본 설정 */
    private User getUser() {
        User user = User.builder()
            .id(1L)
            .name("이름")
            .email("email@email.com")
            .password("password")
            .phone("01000000000")
            .build();

        return user;
    }

}
