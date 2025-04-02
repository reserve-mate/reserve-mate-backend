package com.reservemate.reserve_mate_backend.match.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class MatchPlayerRepositoryTest {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private MatchPlayerRepository matchPlayerRepository;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacilityManagerRepository facilityManagerRepository;

    private User user;
    private Facility facility;
    private Court court;
    private Match match;
    private FacilityManager facilityManager;

    @BeforeEach
    void setup() {
        user = getUser();
        facility = getFacility();
        court = getCourt(facility);
        facilityManager = getFacilityManager(user, facility);
        match = getMatch(court, facilityManager);
    }

    @Test
    @DisplayName("준비된 매치 플레이어 수")
    void countReadyPlayer() {
        /* given */
        Match match = getMatch(court, facilityManager);

        saveMatchPlayers(match);

        /* when */
        int playerCnt = matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY);

        Assertions.assertThat(playerCnt).isEqualTo(10L);
    }

    private void saveMatchPlayers(Match match) {
        for (int i = 0; i < 10; i++) {
            User loopUser = User.builder()
                .name("이름" + (i + 1))
                .email("email" + (i + 100) + "@email.com")
                .password("password")
                .phone("010000000" + (i + 1))
                .build();

            User saveUser = userRepository.save(loopUser);

            MatchPlayer player = MatchPlayer.builder()
                .status(PlayerStatus.READY)
                .user(saveUser)
                .match(match)
                .build();

            matchPlayerRepository.save(player);
        }
    }

    @Test
    @DisplayName("매치 신청")
    void registPlayerTest() {
        /* given */
        MatchPlayer matchPlayer = MatchPlayer.builder()
            .status(PlayerStatus.READY)
            .user(user)
            .match(match)
            .build();

        /* when */
        MatchPlayer saveMatchPlayer = matchPlayerRepository.save(matchPlayer);

        /* then */
        assertThat(saveMatchPlayer.getStatus()).isEqualTo(matchPlayer.getStatus());
        assertThat(saveMatchPlayer.getUser().getName()).isEqualTo(user.getName());
    }

    private Match getMatch(Court court, FacilityManager manager) {
        Match match = Match.builder()
            .matchName("매치")
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(18)
            .matchDate(LocalDate.now())
            .matchTime(18)
            .endTime(20)
            .matchPrice(11000)
            .court(court)
            .facilityManager(manager)
            .build();

        Match saveMatch = matchRepository.save(match);
        return saveMatch;
    }

    // 매니저 데이터 저장
    private FacilityManager getFacilityManager(User user, Facility facility) {
        FacilityManager manager = FacilityManager.builder()
            .facility(facility)
            .user(user)
            .build();

        FacilityManager saveManager = facilityManagerRepository.save(manager);
        return saveManager;
    }

    private Facility getFacility() {
        Address address = Address.builder()
            .city("서울")
            .build();

        Facility facility = Facility.builder()
            .name("시설")
            .sportType(SportType.FUTSAL)
            .address(address)
            .conventient("1010")
            .build();

        Facility saveFacility = facilityRepository.save(facility);
        return saveFacility;
    }

    private Court getCourt(Facility facility) {

        Court court = Court.builder()
            .name("운동 코트")
            .courtType(CourtType.ARTIFICIAL_TURF_FUTSAL)
            .width(20)
            .height(40)
            .indoor(false)
            .facility(facility)
            .build();
        Court saveUser = courtRepository.save(court);
        return saveUser;
    }

    /* 회원 기본 설정 */
    private User getUser() {
        User user = User.builder()
            .name("이름")
            .email("email@email.com")
            .password("password")
            .phone("01000000000")
            .build();
        User savUser = userRepository.save(user);
        return savUser;
    }

}
