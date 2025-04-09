package com.reservemate.reserve_mate_backend.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

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
import com.reservemate.reserve_mate_backend.payment.domain.PaymentStatus;
import com.reservemate.reserve_mate_backend.payment.dto.request.ConfirmRequestDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.PaymentHistReqDto;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResDto;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentResponse;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentCustomRepository;
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

    @Mock
    private PaymentCustomRepository paymentCustomRepository;

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
    @DisplayName("결제 내역 테스트")
    void testGetPaymentHistory() {
        /* given */
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        Pageable pageable = PageRequest.of(0, 10);
        PaymentHistReqDto histReq = PaymentHistReqDto.builder()
            .userId(user.getId())
            .payType("match")
            .build();

        List<PaymentHistResDto> histResDtos = getMockPaymentList();

        Slice<PaymentHistResDto> mockPayments = mock(Slice.class);
        /* UnnecessaryStubbingException : 불필요한 stubbing이 있을 경우 발생 (아마 기존에 paymentCustomRepository과 중복으로 발생 추정)
         * lenient() -> stubbing 무시 
         */
        lenient().when(mockPayments.getContent()).thenReturn(histResDtos);
        lenient().when(mockPayments.hasNext()).thenReturn(false);
        lenient().when(mockPayments.getSize()).thenReturn(histResDtos.size());

        given(paymentCustomRepository.getMatchPayHist(user.getId(), null, pageable))
            .willReturn(mockPayments);

        /* when */
        Slice<PaymentHistResDto> payments = paymentService.getPaymentHistory(histReq);

        /* then */
        assertThat(payments.getSize()).isEqualTo(histResDtos.size());
        assertThat(payments.getContent().get(0).getMatchName()).isEqualTo(histResDtos.get(0).getMatchName());
        assertThat(payments.getContent().get(0).getAmount()).isEqualTo(histResDtos.get(0).getAmount());
    }

    /* 결제 목록 */
    private List<PaymentHistResDto> getMockPaymentList() {

        List<PaymentHistResDto> histResDtos = new ArrayList<>();

        for (int i = 1; i <= 2; i++) {
            PaymentHistResDto payment = PaymentHistResDto.builder()
                .paymentId(Long.valueOf(i))
                .amount(11000)
                .paymentMethod(PaymentMethod.CARD)
                .paymentStatus(PaymentStatus.PAID)
                .paidAt(LocalDateTime.now().toString())
                .matchName("매치" + i)
                .build();

            histResDtos.add(payment);
        }

        return histResDtos;
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
