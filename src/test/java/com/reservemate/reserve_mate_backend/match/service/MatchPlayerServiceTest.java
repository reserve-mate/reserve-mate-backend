package com.reservemate.reserve_mate_backend.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.domain.PlayerStatus;
import com.reservemate.reserve_mate_backend.match.dto.request.CancelPlayerDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.dto.request.ApplyPlayerDto;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class MatchPlayerServiceTest {

    @Mock
    private MatchPlayerRepository matchPlayerRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MatchPlayerService matchPlayerService;

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
    @DisplayName("매치 취소")
    void testCancelMatchRequest() {
        MatchPlayer matchPlayer = getMatchPlayer();

        /* when */
        matchPlayerService.cancelMatchRequest(new CancelPlayerDto(matchPlayer));

        /* then */
        assertThat(matchPlayer.getStatus()).isEqualTo(PlayerStatus.CANCEL);
    }

    private MatchPlayer getMatchPlayer() {
        MatchPlayer matchPlayer = MatchPlayer.builder()
            .status(PlayerStatus.READY)
            .match(match)
            .user(user)
            .build();
        return matchPlayer;
    }

    @Test
    @DisplayName("매치 신청 테스트")
    void testApplyForMatch() {
        /* given */
        ApplyPlayerDto applyPlayerDto = new ApplyPlayerDto(user, match);
        given(matchPlayerRepository.countByMatchAndStatus(match, PlayerStatus.READY)).willReturn(3);

        ArgumentCaptor<MatchPlayer> argumentCaptor = ArgumentCaptor.forClass(MatchPlayer.class);

        /* when */
        matchPlayerService.applyForMatch(applyPlayerDto);

        /* then */
        verify(matchPlayerRepository, times(1)).save(argumentCaptor.capture());

        MatchPlayer matchPlayer = argumentCaptor.getValue();

        assertThat(matchPlayer.getMatch().getCourt().getName()).isEqualTo(court.getName());
    }

    private Match getMatch(Court court, String userName) {
        Match match = Match.builder()
            .matchId(1L)
            //.manager(userName)
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(18)
            .matchDate(LocalDate.now().plusDays(1))
            .matchTime(18)
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
        Court court = new Court(1L, "운동 코트", CourtType.ARTIFICIAL_TURF_FUTSAL, 20, 40, false, facility);

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
