package com.reservemate.reserve_mate_backend.match.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
    @DisplayName("준비된 매치 플레이어 수")
    void countReadyPlayer() {
        /* given */
        Court saveCourt = courtRepository.save(court);
        Match match = getMatch(saveCourt);

        saveMatchPlayers(match);

        /* when */
        int playerCnt = matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY);

        Assertions.assertThat(playerCnt).isEqualTo(10L);
    }

    private void saveMatchPlayers(Match match) {
        for (int i = 0; i < 10; i++) {
            User user = User.builder()
                .name("이름" + (i + 1))
                .email("email" + (i + 1) + "@email.com")
                .password("password")
                .phone("010000000" + (i + 1))
                .build();

            User saveUser = userRepository.save(user);

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
        Court saveCourt = courtRepository.save(court);
        Match match = getMatch(saveCourt);
        User user = getUser();

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
        assertThat(saveMatchPlayer.getMatch().getManager()).isEqualTo(match.getManager());
    }

    /* 회원 기본 설정 */
    private User getUser() {
        User user = User.builder()
            .name("이름")
            .email("email@email.com")
            .password("password")
            .phone("01000000000")
            .build();

        User saveUser = userRepository.save(user);

        return saveUser;
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

        Match saveMatch = matchRepository.save(match);
        return saveMatch;
    }

}
