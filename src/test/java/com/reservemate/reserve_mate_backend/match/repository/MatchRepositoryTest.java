package com.reservemate.reserve_mate_backend.match.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.reservemate.reserve_mate_backend.facility.domain.ManagerRole;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FacilityManagerRepository facilityManagerRepository;

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

    // @Test
    // @DisplayName("매치 인서트")
    // public void insertMatch() {
    //     for (int i = 0; i < 2; i++) {
    //         FacilityManager facilityManager = facilityManagerRepository.findById(22L + i).orElseThrow(
    //             () -> new ApiException(ErrorCode.ADMIN_FORBIDDEN));
    //         for (int j = 0; j < 15; j++) {
    //             Court court = courtRepository.findById(17L + i).orElseThrow(() -> new ApiException(
    //                 ErrorCode.INVALID_EXTENSION));
    //             matchRepository.save(createMatch(j, court, facilityManager));
    //         }
    //     }
    // }

    // private Match createMatch(int i, Court court, FacilityManager manager) {
    //     return Match.builder()
    //         .court(court)
    //         .facilityManager(manager)
    //         .matchDate(LocalDate.of(2025, 7, 9 + i))
    //         .matchName("매치" + i)
    //         .matchPrice(11000)
    //         .matchTime(18)
    //         .endTime(20)
    //         .matchStatus(MatchStatus.APPLICABLE)
    //         .description("test Match")
    //         .teamCapacity(18)
    //         .build();
    // }

    // @Test
    // @DisplayName("매니저 인서트")
    // public void insertFacilityManager() {
    //     Facility facility = facilityRepository.findById(9L).orElseThrow(() -> new ApiException(
    //         ErrorCode.INVALID_EXTENSION));
    //     for (int i = 0; i < 2; i++) {
    //         User user = userRepository.findById(135L + i).orElseThrow(() -> new ApiException(
    //             ErrorCode.ADMIN_FORBIDDEN));
    //         facilityManagerRepository.save(createFacilityManager(i, facility, user));
    //     }
    // }

    // private FacilityManager createFacilityManager(int i, Facility facility, User user) {
    //     return FacilityManager.builder()
    //         .facility(facility)
    //         .user(user)
    //         .managerRole(ManagerRole.MANAGER)
    //         .build();
    // }

    // @Test
    // @DisplayName("유저 생성")
    // void insertUser() {
    //     for (int i = 16; i <= 30; i++) {
    //         userRepository.save(userCreate(i));
    //     }
    // }

    // private User userCreate(int i) {
    //     String phone = "010-0002-000" + i;
    //     if (i > 9) {
    //         phone = "010-0002-00" + i;
    //     } else if (i > 99) {
    //         phone = "010-0002-0" + i;
    //     }

    //     return User.builder()
    //         .email("manager" + i + "@example.com")
    //         .password(passwordEncoder.encode("1234"))
    //         .phone(phone)
    //         .name("매니저" + i)
    //         .role(UserRole.ROLE_FACILITY_MANAGER)
    //         .build();
    // }

    @Test
    @DisplayName("시간 지난 매치 상태 업데이트")
    void testUpdateBeforeMatchs() {
        /* given */
        List<Match> matches = saveMatches();
        List<Long> matchIds = Arrays.asList(1L, 2L);

        /* when */
        matchRepository.updateBeforeMatchs(matchIds);

        /* then */
        for (Match match : matches) {
            if (match.getMatchStatus() == MatchStatus.END) {
                assertThat(match.getMatchStatus()).isEqualTo(MatchStatus.END);
            }
        }
    }

    @Test
    @DisplayName("현재 시간 종료 매치 상태값 수정")
    void testUpdateEndBeforeMatch() {
        /* given */
        List<Match> matches = saveMatches();
        int matchTime = Utils.getNowTime();

        /* when */
        matchRepository.updateEndBeforeMatch(LocalDate.now(), matchTime, MatchStatus.END);

        /* then */
        for (int i = 0; i < matches.size(); i++) {
            if (matches.get(i).getMatchStatus() == MatchStatus.END) {
                assertThat(matches.get(i).getMatchStatus()).isEqualTo(MatchStatus.END);
            }
        }
    }

    private List<Match> saveMatches() {
        List<Match> matches = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Match saveMatch = Match.builder()
                .matchName("매치" + i)
                .matchStatus(MatchStatus.APPLICABLE)
                .teamCapacity(18)
                .matchDate(LocalDate.now())
                .matchTime(20 + i)
                .endTime(21 + i)
                .matchPrice(11000)
                .court(court)
                .facilityManager(facilityManager)
                .build();

            Match realMatch = matchRepository.save(saveMatch);
            matches.add(realMatch);
        }
        return matches;
    }

    @Test
    @DisplayName("해당 날짜 매치 목록 조회")
    void testFindByMatchDateAndCourt() {

        /* when */
        List<Match> matches = matchRepository.findByMatchDateAndCourt(LocalDate.now(), court);

        /* then */
        assertThat(matches.isEmpty()).isFalse();
    }

    @Test
    @DisplayName("매치 중복 검사")
    void dupleMatchTest() {
        /* given */
        LocalDate date = LocalDate.of(2025, 3, 26);
        int matchTime = 16;

        /* when */
        boolean isDuple = matchRepository.existsByMatchDateAndMatchTimeAndCourt(date, matchTime, court);

        Assertions.assertThat(isDuple).isFalse();
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
            .managerRole(ManagerRole.MANAGER)
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
            .fee(0)
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
