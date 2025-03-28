package com.reservemate.reserve_mate_backend.match.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.user.domain.User;

public class MatchTest {

    private User user;
    private Facility facility;
    private Court court;
    private Match match;

    @BeforeEach
    void setUp() {
        user = getUser();
        facility = getFacility();
        court = getCourt(facility);
        match = getMatch(court, user.getName());
    }

    @Test
    @DisplayName("날짜가 지난 매치인지 검증")
    void testIsOverMatch() {
        /* given */

        /* then */
        assertThatThrownBy(() -> match.isOverMatch())
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 시작되거나 종료된 매치입니다.");
    }

    private Match getMatch(Court court, String userName) {
        Match match = Match.builder()
            .matchId(1L)
            .manager(userName)
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(18)
            .matchDate(LocalDate.now())
            .matchTime(12)
            .matchPrice(11000)
            .court(court)
            .build();
        return match;
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

        Court court = Court.builder()
            .id(1L)
            .name("운동 코트")
            .sportType(SportType.FUTSAL)
            .capacity(12)
            .indoor(false)
            .facility(facility)
            .build();

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
