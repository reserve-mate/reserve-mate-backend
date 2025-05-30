package com.reservemate.reserve_mate_backend.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
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
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.client.PayClient;
import com.reservemate.reserve_mate_backend.payment.client.impl.PayClientImpl;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentResponse;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentCustomRepository;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import okhttp3.mockwebserver.MockWebServer;

// @ExtendWith(MockitoExtension.class)
@SpringBootTest
@Transactional
public class MatchServiceConcurrencyTest {

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private FacilityRepository facilityRepository;

    @MockitoBean
    private CourtRepository courtRepository;

    @MockitoBean
    private FacilityManagerRepository facilityManagerRepository;

    @MockitoBean
    private MatchRepository matchRepository;

    @MockitoBean
    private PayClient payClient;

    @MockitoBean
    private JwtUtil jwtUtil;

    @Autowired
    private PaymentService paymentService;

    private User user;
    private Facility facility;
    private Court court;
    private Match match;
    private FacilityManager facilityManager;

    @BeforeEach
    void setUp(TestInfo testInfo) throws Exception {
        user = getUser();
        facility = getFacility();
        court = getCourt(facility);
        facilityManager = getFacilityManager(user, facility);
        match = getMatch(court, facilityManager);

    }

    @Test
    @DisplayName("결제 승인 동시성 테스트")
    void testRequestPayConfirmThread() throws InterruptedException, IOException {

        List<Boolean> results = Collections.synchronizedList(new ArrayList<>());

        int numberOfThread = match.getTeamCapacity();
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThread);
        for (int i = 0; i < numberOfThread; i++) {
            service.submit(() -> {
                // 실행될 로직
                try {
                    SaveAmountRequest amountRequest = SaveAmountRequest.builder()
                        .amount(match.getMatchPrice())
                        .orderId(UUID.randomUUID().toString())
                        .paymentKey("tviva20250409200902SF27")
                        .matchId(match.getMatchId())
                        .build();

                    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
                    String fakeAccessToken = "mocked.jwt.token";

                    given(request.getHeader("access")).willReturn(fakeAccessToken);
                    given(jwtUtil.getId(fakeAccessToken)).willReturn(user.getId());
                    given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

                    HttpResponse<String> mockResponse = mock(HttpResponse.class);
                    when(mockResponse.statusCode()).thenReturn(200);
                    given(payClient.requestPay(any(), any(), anyInt())).willReturn(mockResponse);

                    /* when */
                    paymentService.requestPayConfirm(request, amountRequest);
                    results.add(true);

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    results.add(false);
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        /* then */
        long successCnt = results.stream().filter(result -> !result).count(); // 해당 결과가 true은 List 요소의 개수
        assertThat(successCnt).isEqualTo(numberOfThread);
    }

    // 매니저 데이터 저장
    private FacilityManager getFacilityManager(User user, Facility facility) {
        FacilityManager manager = new FacilityManager(1L, facility, user);
        return manager;
    }

    /* 매치 플레이어 세팅 */
    private MatchPlayer getMatchPlayer(User user, Match match) {
        MatchPlayer matchPlayer = MatchPlayer.builder()
            .playerId(1L)
            .user(user)
            .match(match)
            .build();
        return matchPlayer;
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

    private Facility getFacility() {
        Address address = Address.builder()
            .city("서울")
            .build();

        Facility facility = Facility.builder()
            .name("시설")
            .address(address)
            .description("123")
            .conventient("1100")
            .sportType(SportType.BADMINTON)
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
