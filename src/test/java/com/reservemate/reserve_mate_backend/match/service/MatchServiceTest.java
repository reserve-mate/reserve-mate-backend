package com.reservemate.reserve_mate_backend.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityImage;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityImageRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.dto.request.CreateMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.request.ModifyMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDetailDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class MatchServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchPlayerRepository matchPlayerRepository;

    @Mock
    private FacilityImageRepository facilityImageRepository;

    @InjectMocks
    private MatchService matchService;

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
    void testGetMatch() {
        /* given */
        List<MatchPlayer> matchPlayers = new ArrayList();
        List<FacilityImage> facilityImages = new ArrayList();

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(matchRepository.findById(match.getMatchId())).willReturn(Optional.of(match));
        given(matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY))
            .willReturn(matchPlayers);
        given(facilityImageRepository.findByFacility(facility)).willReturn(facilityImages);

        /* when */
        MatchDetailDto matchDetailDto = matchService.getMatch(match.getMatchId(), user.getId());

        /* then */
        assertThat(matchDetailDto.getManager()).isEqualTo(user.getName());
        assertThat(matchDetailDto.getMatchDate()).isEqualTo(Utils.localDateFormatWeek(match.getMatchDate()));
        assertThat(matchDetailDto.getMatchPrice()).isEqualTo(match.getMatchPrice());
        assertThat(matchDetailDto.getCourtName()).isEqualTo(court.getName());
    }

    @Test
    @DisplayName("매치 정보 수정 테스트")
    void modifyMatchTest() {
        /* given */
        given(matchRepository.findById(match.getMatchId()))
            .willReturn(Optional.of(match));
        given(matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY))
            .willReturn(6);

        ModifyMatchDto modifyMatchDto = ModifyMatchDto.builder()
            .description("매치 정보 수정 테스트입니다.")
            .teamCapacity(7)
            .build();

        /* when */
        matchService.modifyMatch(match.getMatchId(), modifyMatchDto);

        /* then */
        assertThat(match.getDescription()).isEqualTo(modifyMatchDto.getDescription());
        assertThat(match.getTeamCapacity()).isEqualTo(modifyMatchDto.getTeamCapacity());
    }

    @Test
    @DisplayName("종료된 매치인지 검사 - 이미 종료된 매치입니다.")
    void testIsFinishMatch() {
        /* given */
        match.chgFinish();

        given(matchRepository.findById(match.getMatchId()))
            .willReturn(Optional.of(match));

        ModifyMatchDto modifyMatchDto = new ModifyMatchDto();

        /* then */
        assertThatThrownBy(() -> matchService.modifyMatch(match.getMatchId(), modifyMatchDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 종료된 매치입니다.");
    }

    @Test
    @DisplayName("매치 중복 테스트 - 예외처리가 되어야함")
    void dupleMatchTest() {
        /* given */
        CreateMatchDto createMatchDto = getCreateMatchDto(user, court);

        given(courtRepository.findById(court.getId())).willReturn(Optional.of(court));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(matchRepository.existsByMatchDateAndMatchTimeAndCourt(
            createMatchDto.getMatchDate(), createMatchDto.getMatchTime(), court)).willReturn(true);

        assertThatThrownBy(() -> matchService.registMatch(createMatchDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 등록된 매치가 있습니다.");
    }

    @Test
    @DisplayName("매치 등록 테스트 - User와 Court 정보가 있어야함")
    void testRegistMatch() {
        /* given */
        CreateMatchDto createMatchDto = getCreateMatchDto(user, court);

        given(courtRepository.findById(court.getId())).willReturn(Optional.of(court));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(matchRepository.existsByMatchDateAndMatchTimeAndCourt(
            createMatchDto.getMatchDate(), createMatchDto.getMatchTime(), court)).willReturn(false);
        ArgumentCaptor<Match> arguMatch = ArgumentCaptor.forClass(Match.class);

        /* when */
        matchService.registMatch(createMatchDto);

        /* then */
        verify(matchRepository, times(1)).save(arguMatch.capture());

        Match saveMatch = arguMatch.getValue();

        assertThat(saveMatch.getTeamCapacity()).isEqualTo(createMatchDto.getTeamCapacity());
        assertThat(saveMatch.getMatchDate()).isEqualTo(createMatchDto.getMatchDate());
        assertThat(saveMatch.getMatchTime()).isEqualTo(createMatchDto.getMatchTime());
    }

    private Match getMatch(Court court, String userName) {
        Match match = Match.builder()
            .matchId(1L)
            .manager(userName)
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(18)
            .matchDate(LocalDate.now())
            .matchTime(18)
            .matchPrice(11000)
            .court(court)
            .build();
        return match;
    }

    /* 매치 등로 데이터 세팅 */
    private CreateMatchDto getCreateMatchDto(User user, Court court) {

        CreateMatchDto createMatchDto = CreateMatchDto.builder()
            .userId(user.getId())
            .teamCapacity(15)
            .matchDate(LocalDate.now())
            .matchTime(10)
            .courtId(court.getId())
            .matchPrice(11000)
            .build();

        return createMatchDto;
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
