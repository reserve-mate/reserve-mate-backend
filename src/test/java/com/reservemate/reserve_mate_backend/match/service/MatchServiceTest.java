package com.reservemate.reserve_mate_backend.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.reservemate.reserve_mate_backend.facility.domain.ManagerRole;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.util.Utils;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityImage;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityImageRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.dto.request.ModifyMatchDto;
import com.reservemate.reserve_mate_backend.match.dto.respone.MatchDetailDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.domain.UserRole;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class MatchServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchPlayerRepository matchPlayerRepository;

    @Mock
    private FacilityImageRepository facilityImageRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private MatchService matchService;

    private User user;
    private Facility facility;
    private Court court;
    private Match match;
    private FacilityManager facilityManager;

    @Captor
    private ArgumentCaptor<List<MatchPlayer>> argumentCaptors;

    @BeforeEach
    void setUp() {
        user = getUser();
        facility = getFacility();
        court = getCourt(facility);
        facilityManager = getFacilityManager(user, facility);
        match = getMatch(court, facilityManager);
    }

    @Test
    @DisplayName("매치 삭제")
    void testDeleteMatch() {
        /* given */
        User adminUser = User.builder()
            .id(2L)
            .name("이름")
            .email("email@email.com")
            .password("password")
            .phone("01000000000")
            .role(UserRole.ROLE_ADMIN)
            .build();

        given(userRepository.findById(adminUser.getId())).willReturn(Optional.of(adminUser));
        given(matchRepository.findById(match.getMatchId())).willReturn(Optional.of(match));

        List<MatchPlayer> matchPlayers = getMatchPlayers();
        given(matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY))
            .willReturn(matchPlayers);

        /* when */
        matchService.deleteMatch(match.getMatchId(), adminUser.getId());

        /* then */
        verify(matchPlayerRepository, times(1))
            .updatePlayersMatchRemoved(match.getMatchId(), PlayerStatus.MATCH_CANCELLED);

        verify(matchRepository, times(1)).delete(match);
    }

    private List<MatchPlayer> getMatchPlayers() {
        List<MatchPlayer> matchPlayers = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {

            User loopUser = User.builder()
                .id(Long.valueOf(i))
                .name("이름")
                .email("email@email.com")
                .password("password")
                .phone("01000000000")
                .build();

            MatchPlayer matchPlayer = MatchPlayer.builder()
                .playerId(Long.valueOf(i))
                .status(PlayerStatus.READY)
                .user(loopUser)
                .match(match)
                .build();

            matchPlayers.add(matchPlayer);
        }

        return matchPlayers;
    }

    @Test
    void testGetMatch() {
        /* given */
        List<MatchPlayer> matchPlayers = new ArrayList<>();
        List<FacilityImage> facilityImages = new ArrayList<>();

        Payment payment = Payment.builder()
            .impUid(UUID.randomUUID().toString())
            .match(match)
            .user(user)
            .build();

        // HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        // String fakeAccessToken = "mocked.jwt.token";

        // given(request.getHeader("access")).willReturn(fakeAccessToken);
        // given(jwtUtil.getId(fakeAccessToken)).willReturn(user.getId());
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(paymentRepository.findByMatchIdAndUserId(match.getMatchId(), user.getId())).willReturn(Optional.of(
            payment));
        given(matchRepository.findById(match.getMatchId())).willReturn(Optional.of(match));
        given(matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY))
            .willReturn(matchPlayers);
        given(facilityImageRepository.findByFacility(facility)).willReturn(facilityImages);

        /* when */
        MatchDetailDto matchDetailDto = matchService.getMatch(user.getId(), user.getId());

        /* then */
        assertThat(matchDetailDto.getUserDataDto().getUserName()).isEqualTo(user.getName());
        assertThat(matchDetailDto.getMatchDataDto().getMatchDate()).isEqualTo(Utils.localDateFormatWeek(match
            .getMatchDate()));
        assertThat(matchDetailDto.getUserDataDto().getOrderId()).isEqualTo(payment.getImpUid());
        assertThat(matchDetailDto.getMatchDataDto().getMatchPrice()).isEqualTo(match.getMatchPrice());
        assertThat(matchDetailDto.getFacilityDataDto().getCourtName()).isEqualTo(court.getName());
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
        match.chgEndMatch();

        given(matchRepository.findById(match.getMatchId()))
            .willReturn(Optional.of(match));

        ModifyMatchDto modifyMatchDto = new ModifyMatchDto();

        /* then */
        assertThatThrownBy(() -> matchService.modifyMatch(match.getMatchId(), modifyMatchDto))
            .isInstanceOf(ApiException.class)
            .hasMessage("이미 진행중 또는는 종료된 매치입니다.");
    }

    private Match getMatch(Court court, FacilityManager manager) {
        Match match = Match.builder()
            .matchId(1L)
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(18)
            .matchDate(LocalDate.now())
            .matchTime(18)
            .endTime(20)
            .matchPrice(11000)
            .court(court)
            .facilityManager(manager)
            .build();
        return match;
    }

    // 매니저 데이터 저장
    private FacilityManager getFacilityManager(User user, Facility facility) {
        FacilityManager manager = new FacilityManager(1L, facility, user, ManagerRole.MANAGER);
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
            .role(UserRole.ROLE_FACILITY_MANAGER)
            .build();

        return user;
    }

}
