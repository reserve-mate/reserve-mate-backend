package com.reservemate.reserve_mate_backend.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentMethod;
import com.reservemate.reserve_mate_backend.payment.dto.request.ConfirmRequestDto;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentResponse;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PaymentService paymentService;

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
    @DisplayName("결제 요청 중복 테스트")
    void testPayConfirmDuplication() {
        /* given */
        ConfirmRequestDto confirmRequestDto = getConfirmRequestDto(user, match);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(matchRepository.findById(match.getMatchId())).willReturn(Optional.of(match));
        given(paymentRepository.existsPayment(user.getId(), match.getMatchId())).willReturn(true);

        /* then */
        assertThrows(ApiException.class, () -> paymentService.requestPayment(confirmRequestDto));
    }

    @Test
    @DisplayName("결제 요청 테스트")
    void testRequestPayConfirm() {
        /* given */
        ConfirmRequestDto confirmRequestDto = getConfirmRequestDto(user, match);
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        given(matchRepository.findById(match.getMatchId())).willReturn(Optional.of(match));
        Payment payment = getPayment();
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);
        given(paymentRepository.existsPayment(user.getId(), match.getMatchId())).willReturn(false);

        /* when */
        PaymentResponse returnResponse = paymentService.requestPayment(confirmRequestDto);

        /* then */

        assertThat(returnResponse.getAmount()).isEqualTo(confirmRequestDto.getAmount());
        assertThat(returnResponse.getOrderId()).isEqualTo(payment.getImpUid());
        assertThat(returnResponse.getSuccessUrl()).isEqualTo(confirmRequestDto.getSuccessUrl());
        assertThat(returnResponse.getFailUrl()).isEqualTo(confirmRequestDto.getFailUrl());
    }

    private Payment getPayment() {
        Payment payment = Payment.builder()
            .id(1L)
            .impUid(UUID.randomUUID().toString())
            .amount(11000)
            .user(user)
            .match(match)
            .build();
        return payment;
    }

    private ConfirmRequestDto getConfirmRequestDto(User user, Match match) {
        ConfirmRequestDto confirmRequestDto = ConfirmRequestDto.builder()
            .paymentMethod(PaymentMethod.CARD)
            .orderId(UUID.randomUUID().toString())
            .amount(11000)
            .userId(user.getId())
            .matchId(match.getMatchId())
            .successUrl("http://localhost:8080/payment/success")
            .failUrl("http://localhost:8080/payment/fail")
            .build();
        return confirmRequestDto;
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

        Court court = new Court(1L, "운동 코트", CourtType.ARTIFICIAL_TURF_FUTSAL, 20, 40, false, facility);

        // Court court = Court.builder()
        //     .id(1L)
        //     .name("운동 코트")
        //     //.sportType(SportType.FUTSAL)
        //     //.capacity(12)
        //     .indoor(false)
        //     .facility(facility)
        //     .build();

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
