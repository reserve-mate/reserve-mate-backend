package com.reservemate.reserve_mate_backend.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
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

@ExtendWith(MockitoExtension.class)
@Transactional
public class MatchServiceConcurrencyTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCustomRepository paymentCustomRepository;

    @Mock
    private MatchPlayerRepository matchPlayerRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private PayClient payClient;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PaymentService paymentService;

    private PayClient tossClient;

    private User user;
    private Facility facility;
    private Court court;
    private Match match;
    private FacilityManager facilityManager;

    private MockWebServer mockWebServer;
    private String mockWebServerUrl;

    private String successBody;
    private String failBody;

    @BeforeEach
    void setUp(TestInfo testInfo) throws IOException {
        user = getUser();
        facility = getFacility();
        court = getCourt(facility);
        facilityManager = getFacilityManager(user, facility);
        match = getMatch(court, facilityManager);

        tossClient = new PayClientImpl();

        if (testInfo.getDisplayName().equals("결제 취소 상태 체크")) { // 해당 테스트 아래 로직 건너뛰기
            return;
        }

        mockWebServer = new MockWebServer();
        mockWebServer.start();
        mockWebServerUrl = mockWebServer.url("http://localhost:" + this.mockWebServer.getPort()).toString();

        successBody = "{\n" +
            "  \\\"mId\\\": \\\"tosspayments\\\",\n" +
            "  \\\"version\\\": \\\"2022-11-16\\\",\n" +
            "  \\\"paymentKey\\\": \\\"B1d9edx08u7ic9yQqcTzj\\\",\n" +
            "  \\\"status\\\": \\\"DONE\\\",\n" +
            "  \\\"lastTransactionKey\\\": \\\"Wgz12DHTz2PaVxm5LUO6i\\\",\n" +
            "  \\\"method\\\": \\\"간편결제\\\",\n" +
            "  \\\"orderId\\\": \\\"chdimFOf9tnXV5u8Xqtlo\\\",\n" +
            "  \\\"orderName\\\": \\\"토스 티셔츠 외 2건\\\",\n" +
            "  \\\"requestedAt\\\": \\\"2022-06-08T15:40:09+09:00\\\",\n" +
            "  \\\"approvedAt\\\": \\\"2022-06-08T15:40:49+09:00\\\",\n" +
            "  \\\"useEscrow\\\": false,\n" +
            "  \\\"cultureExpense\\\": false,\n" +
            "  \\\"card\\\": {\n" +
            "    \\\"issuerCode\\\": \\\"61\\\",\n" +
            "    \\\"acquirerCode\\\": \\\"31\\\",\n" +
            "    \\\"number\\\": \\\"12345678****789*\\\",\n" +
            "    \\\"installmentPlanMonths\\\": 0,\n" +
            "    \\\"isInterestFree\\\": false,\n" +
            "    \\\"interestPayer\\\": null,\n" +
            "    \\\"approveNo\\\": \\\"00000000\\\",\n" +
            "    \\\"useCardPoint\\\": false,\n" +
            "    \\\"cardType\\\": \\\"신용\\\",\n" +
            "    \\\"ownerType\\\": \\\"개인\\\",\n" +
            "    \\\"acquireStatus\\\": \\\"READY\\\",\n" +
            "    \\\"amount\\\": 15000\n" +
            "  },\n" +
            "  \\\"virtualAccount\\\": null,\n" +
            "  \\\"transfer\\\": null,\n" +
            "  \\\"mobilePhone\\\": null,\n" +
            "  \\\"giftCertificate\\\": null,\n" +
            "  \\\"cashReceipt\\\": null,\n" +
            "  \\\"cashReceipts\\\": null,\n" +
            "  \\\"discount\\\": null,\n" +
            "  \\\"cancels\\\": null,\n" +
            "  \\\"secret\\\": null,\n" +
            "  \\\"type\\\": \\\"NORMAL\\\",\n" +
            "  \\\"easyPay\\\": {\n" +
            "    \\\"provider\\\": \\\"토스페이\\\",\n" +
            "    \\\"amount\\\": 0,\n" +
            "    \\\"discountAmount\\\": 0\n" +
            "  },\n" +
            "  \\\"country\\\": \\\"KR\\\",\n" +
            "  \\\"failure\\\": null,\n" +
            "  \\\"isPartialCancelable\\\": true,\n" +
            "  \\\"receipt\\\": {\n" +
            "    \\\"url\\\": \\\"https://dashboard.tosspayments.com/sales-slip?transactionId=KAgfjGxIqVVXDxOiSW1wUnRWBS1dszn3DKcuhpm7mQlKP0iOdgPCKmwEdYglIHX&ref=PX\\\"\n"
            +
            "  },\n" +
            "  \\\"checkout\\\": {\n" +
            "    \\\"url\\\": \\\"https://api.tosspayments.com/v1/payments/B1d9edx08u7ic9yQqcTzj/checkout\\\"\n" +
            "  },\n" +
            "  \\\"currency\\\": \\\"KRW\\\",\n" +
            "  \\\"totalAmount\\\": 15000,\n" +
            "  \\\"balanceAmount\\\": 15000,\n" +
            "  \\\"suppliedAmount\\\": 13636,\n" +
            "  \\\"vat\\\": 1364,\n" +
            "  \\\"taxFreeAmount\\\": 0,\n" +
            "  \\\"metadata\\\": null\n" +
            "}";

        failBody = "{\n" +
            "  \\\"code\\\": \\\"NOT_FOUND_PAYMENT_SESSION\\\",\n" +
            "  \\\"message\\\": \\\"결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.\\\"\n" +
            "}";
    }

    @AfterEach
    void terminate(TestInfo testInfo) throws IOException {
        if (testInfo.getDisplayName().equals("결제 취소 상태 체크")) { // 해당 테스트 아래 로직 건너뛰기
            return;
        }
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("결제 승인 동시성 테스트")
    void testRequestPayConfirmThread() throws InterruptedException, IOException {

        List<Boolean> results = Collections.synchronizedList(new ArrayList<>());
        //Semaphore concurrentLimit = new Semaphore(2);

        /* given */
        given(matchRepository.findByIdWithLock(match.getMatchId())).willReturn(Optional.of(match));

        int numberOfThread = match.getTeamCapacity();
        ExecutorService service = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(numberOfThread);
        for (int i = 0; i < numberOfThread; i++) {
            service.submit(() -> {
                // 실행될 로직
                try {
                    //concurrentLimit.acquire();  // 2개까지만 동시 허용됨
                    SaveAmountRequest amountRequest = SaveAmountRequest.builder()
                        .amount(match.getMatchPrice())
                        .orderId(UUID.randomUUID().toString())
                        .paymentKey("tviva20250409200902SF27")
                        .matchId(match.getMatchId())
                        .build();

                    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
                    String fakeAccessToken = "mocked.jwt.token";

                    given(request.getHeader("access")).willReturn(fakeAccessToken);
                    lenient().when(jwtUtil.getId(fakeAccessToken)).thenReturn(user.getId());
                    lenient().when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

                    // 외부 api 가짜 응답
                    HttpResponse<String> mockResponse = mock(HttpResponse.class);
                    when(mockResponse.statusCode()).thenReturn(200);
                    lenient().when(payClient.requestPay(amountRequest.getOrderId(), amountRequest.getPaymentKey(), amountRequest.getAmount())).thenReturn(mockResponse);

                    /* when */
                    paymentService.requestPayConfirm(request, amountRequest);
                    results.add(true);

                } catch (IOException | InterruptedException e) {
                    // TODO Auto-generated catch block
                    results.add(false);
                    e.printStackTrace();
                }

                latch.countDown();
            });
        }
        latch.await();

        /* then */
        long successCnt = results.stream().filter(result -> result).count(); // 해당 결과가 true은 List 요소의 개수
        assertThat(successCnt).isEqualTo(18);
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
