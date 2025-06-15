package com.reservemate.reserve_mate_backend.admin.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.reservemate.reserve_mate_backend.admin.match.dto.request.AdminMatchesRequest;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchDetailResponse;
import com.reservemate.reserve_mate_backend.admin.match.dto.response.AdminMatchesResponse;
import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.facility.repository.CourtRepository;
import com.reservemate.reserve_mate_backend.facility.repository.FacilityManagerRepository;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.repository.MatchCustomRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminMatchServiceTest {

    @Mock
    private MatchCustomRepository matchCustomRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private MatchPlayerRepository matchPlayerRepository;

    @Mock
    private CourtRepository courtRepository;

    @Mock
    private FacilityManagerRepository facilityManagerRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    // @Mock
    // private JwtUtil jwtUtil;

    @InjectMocks
    private AdminMatchService adminMatchService;

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
    @DisplayName("관리자 매치 삭제")
    void testDeleteMatch() throws Exception, SecurityException {
        /* given */
        given(matchRepository.findById(match.getMatchId())).willReturn(Optional.of(match));

        // Field field = BaseEntity.class.getDeclaredField("deleted");
        // field.setAccessible(true);
        // field.set(match, true);

        List<MatchPlayer> matchPlayers = new ArrayList<>();
        MatchPlayer matchPlayer = MatchPlayer.builder()
            .playerId(1L)
            .status(PlayerStatus.READY)
            .match(match)
            .user(user)
            .build();

        matchPlayers.add(matchPlayer);
        given(matchPlayerRepository.findByMatchAndStatus(match, PlayerStatus.READY)).willReturn(matchPlayers);

        /* when */
        adminMatchService.deleteMatch(match.getMatchId());

        /* then */
        assertThat(match.getMatchStatus()).isEqualTo(MatchStatus.CANCELLED);
    }

    @Test
    @DisplayName("관리자 매치 상세 조회 테스트")
    void testGetAdminMatchDetail() throws Exception, SecurityException {
        /* given */
        given(matchRepository.findById(match.getMatchId())).willReturn(Optional.of(match));

        List<MatchPlayer> matchPlayers = new ArrayList<>();
        MatchPlayer matchPlayer = MatchPlayer.builder()
            .playerId(1L)
            .status(PlayerStatus.READY)
            .match(match)
            .user(user)
            .build();

        Field field = BaseEntity.class.getDeclaredField("updatedAt");
        field.setAccessible(true);
        field.set(matchPlayer, LocalDateTime.now());

        matchPlayers.add(matchPlayer);
        List<PlayerStatus> playerStatus = List.of(PlayerStatus.KICKED, PlayerStatus.READY, PlayerStatus.ONGOING,
            PlayerStatus.COMPLETED);
        given(matchPlayerRepository.findByMatchAndStatusIn(match, playerStatus)).willReturn(matchPlayers);

        /* when */
        AdminMatchDetailResponse detailResponse = adminMatchService.getAdminMatchDetail(match.getMatchId());

        /* then */
        assertThat(detailResponse.getMatchTitle()).isEqualTo(match.getMatchName());
        assertThat(detailResponse.getMatchTime()).isEqualTo(match.getMatchTime());
    }

    @Test
    @DisplayName("관리자 매치 목록 조회 테스트")
    void testGetMatches() {

        /* given */
        AdminMatchesRequest matchesRequest = AdminMatchesRequest.builder()
            .build();

        // HttpServletRequest request = mock(HttpServletRequest.class);
        // String fakeAccessToken = "mock.fakeAccessToken";

        // given(request.getHeader("access")).willReturn(fakeAccessToken);
        // given(jwtUtil.getId(fakeAccessToken)).willReturn(user.getId());

        Pageable pageable = PageRequest.of(0, 6);

        List<AdminMatchesResponse> list = getAdminMatchesResponses();

        Slice<AdminMatchesResponse> sliceList = new SliceImpl<>(list, pageable, false);
        given(matchCustomRepository.getAdminMatches(user.getId(), matchesRequest, pageable)).willReturn(sliceList);

        /* when */
        Slice<AdminMatchesResponse> adminMatchesResponses = adminMatchService.getMatches(user.getId(), matchesRequest);

        /* then */
        assertThat(adminMatchesResponses.getContent()).hasSize(sliceList.getSize());
        assertThat(adminMatchesResponses.getContent().get(0).getMatchId()).isEqualTo(list.get(0).getMatchId());
    }

    private List<AdminMatchesResponse> getAdminMatchesResponses() {

        List<AdminMatchesResponse> list = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            Long matchNum = i + 1L;
            AdminMatchesResponse response = AdminMatchesResponse.builder()
                .matchId(matchNum)
                .matchName("matchName" + matchNum)
                .sportType(SportType.FUTSAL)
                .facilityName("facilityName" + matchNum)
                .matchDate(LocalDate.of(2025, 5, (i + 1)))
                .teamCapacity(18)
                .playerCnt(6L)
                .matchTime(14)
                .endTime(16)
                .matchStatus(MatchStatus.APPLICABLE)
                .build();

            list.add(response);
        }

        return list;
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
