package com.reservemate.reserve_mate_backend.match.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;

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
import com.reservemate.reserve_mate_backend.match.dto.request.ApplyMatchDto;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
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
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(matchRepository.findById(match.getMatchId())).willReturn(Optional.of(match));
        given(matchPlayerRepository.findByUserAndMatch(user, match)).willReturn(Optional.of(matchPlayer));

        /* when */
        matchPlayerService.cancelMatchRequest(match.getMatchId(), user.getId());

        /* then */
        assertThat(matchPlayer.getStatus()).isEqualTo(PlayerStatus.CANCEL);
    }

    @Test
    @DisplayName("매치 취소 가능 검증")
    void testIsCancelMatch() {
        /* given */
        MatchPlayer matchPlayer = getMatchPlayer();
        matchPlayer.chgStatusCancel();
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(matchRepository.findById(match.getMatchId())).willReturn(Optional.of(match));
        given(matchPlayerRepository.findByUserAndMatch(user, match)).willReturn(Optional.of(matchPlayer));

        /* then */
        assertThatThrownBy(() -> matchPlayerService.cancelMatchRequest(match.getMatchId(), user.getId()))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 취소된 매치입니다.");
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
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(matchRepository.findById(match.getMatchId())).willReturn(Optional.of(match));
        given(matchPlayerRepository.existsByUserAndMatchAndStatusNot(user, match, PlayerStatus.CANCEL))
            .willReturn(false);

        ApplyMatchDto applyMatchDto = ApplyMatchDto.builder()
            .matchId(match.getMatchId())
            .userId(user.getId())
            .build();

        ArgumentCaptor<MatchPlayer> argumentCaptor = ArgumentCaptor.forClass(MatchPlayer.class);

        /* when */
        matchPlayerService.applyForMatch(applyMatchDto);

        /* then */
        verify(matchPlayerRepository, times(1)).save(argumentCaptor.capture());

        MatchPlayer matchPlayer = argumentCaptor.getValue();

        //assertThat(matchPlayer.getMatch().getManager()).isEqualTo(match.getManager());
        assertThat(matchPlayer.getMatch().getCourt().getName()).isEqualTo(court.getName());
    }

    @Test
    @DisplayName("중복된 신청된 매치가 있는지 검증")
    void testIsDupleMatchApply() {
        /* given */
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(matchRepository.findById(match.getMatchId())).willReturn(Optional.of(match));
        given(matchPlayerRepository.existsByUserAndMatchAndStatusNot(user, match, PlayerStatus.CANCEL))
            .willReturn(true);

        ApplyMatchDto applyMatchDto = ApplyMatchDto.builder()
            .matchId(match.getMatchId())
            .userId(user.getId())
            .build();

        /* then */
        assertThatThrownBy(() -> matchPlayerService.applyForMatch(applyMatchDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("이미 매치 신청 내역이 존재합니다.");
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
