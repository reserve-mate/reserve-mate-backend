package com.reservemate.reserve_mate_backend.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.reservemate.reserve_mate_backend.common.auth.JwtUtil;
import com.reservemate.reserve_mate_backend.common.domain.Address;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.TossApiException;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.CourtType;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.match.domain.MatchPlayer;
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.repository.MatchPlayerRepository;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.client.PayClient;
import com.reservemate.reserve_mate_backend.payment.client.impl.PayClientImpl;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentMethod;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentStatus;
import com.reservemate.reserve_mate_backend.payment.dto.request.CancelPayRequestDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.CancelPaymentDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.ConfirmRequestDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.PaymentHistReqDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.RequestPaymentDto;
import com.reservemate.reserve_mate_backend.payment.dto.request.SaveAmountRequest;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentHistResDto;
import com.reservemate.reserve_mate_backend.payment.dto.response.PaymentResponse;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentCustomRepository;
import com.reservemate.reserve_mate_backend.payment.repository.PaymentRepository;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

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
    void setUp() throws IOException {
        user = getUser();
        facility = getFacility();
        court = getCourt(facility);
        facilityManager = getFacilityManager(user, facility);
        match = getMatch(court, facilityManager);

        tossClient = new PayClientImpl();

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
    void terminate() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("결제 취소 리팩토링 테스트[결제 취소 처리 성공]")
    void testCancelPayment() throws Exception {
        /* given */
        Payment payment = getPayment();
        payment.markAsPaid("결제완료일련번호");
        MatchPlayer matchPlayer = getMatchPlayer(payment.getUser(), payment.getMatch());

        CancelPaymentDto cancelPaymentDto = new CancelPaymentDto(matchPlayer, "단순 변심");
        given(paymentRepository.findByMatchAndUser(matchPlayer.getMatch(), matchPlayer.getUser())).willReturn(Optional
            .of(payment));
        int refundAmount = payment.refundAmount();

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(payClient.requestCancelPay(payment.getMerchantUid(), cancelPaymentDto.getCancelReason(), refundAmount))
            .thenReturn(mockResponse);

        /* when */
        paymentService.cancelPayment(cancelPaymentDto);

        /* then */
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        assertThat(payment.getRefundAmount()).isEqualTo(refundAmount);

    }

    @Test
    @DisplayName("결제 취소 리팩토링 테스트[Exception toss 결제 처리가 안된 경우]")
    void testCancelPaymentTossException() throws Exception {
        /* given */
        Payment payment = getPayment();
        payment.markAsPaid("결제완료일련번호");
        MatchPlayer matchPlayer = getMatchPlayer(payment.getUser(), payment.getMatch());

        CancelPaymentDto cancelPaymentDto = new CancelPaymentDto(matchPlayer, "단순 변심");
        given(paymentRepository.findByMatchAndUser(matchPlayer.getMatch(), matchPlayer.getUser())).willReturn(Optional
            .of(payment));
        int refundAmount = payment.refundAmount();

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("{\"message\":\"결제 취소 실패\"}");
        when(payClient.requestCancelPay(payment.getMerchantUid(), cancelPaymentDto.getCancelReason(), refundAmount))
            .thenReturn(mockResponse);

        /* then */
        assertThatThrownBy(() -> paymentService.cancelPayment(cancelPaymentDto))
            .isInstanceOf(TossApiException.class)
            .hasMessage("결제 취소 실패");

    }

    @Test
    @DisplayName("결제 취소 리팩토링 테스트[Exception PAID 상태가 아닌 결제인 경우]")
    void testCancelPaymentException() {
        /* given */
        Payment payment = getPayment();
        MatchPlayer matchPlayer = getMatchPlayer(payment.getUser(), payment.getMatch());

        CancelPaymentDto cancelPaymentDto = new CancelPaymentDto(matchPlayer, "단순 변심");
        given(paymentRepository.findByMatchAndUser(matchPlayer.getMatch(), matchPlayer.getUser())).willReturn(Optional
            .of(payment));

        payment.markAsFailed(); // 실패로 상태 변경

        /* then */
        assertThatThrownBy(() -> paymentService.cancelPayment(cancelPaymentDto))
            .isInstanceOf(ApiException.class)
            .hasMessage("결제된 이력이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("결제 취소 실패 테스트")
    void testRequestCancelPaymentFail() throws Exception {
        /* given */
        Payment payment = getPayment();
        MatchPlayer matchPlayer = getMatchPlayer(payment.getUser(), payment.getMatch());

        CancelPayRequestDto cancelPayRequestDto = CancelPayRequestDto.builder()
            .paymentKey("tviva20250410231140WtiG4")
            .cancelReason("단순 변심")
            .cancelAmount(11000)
            .build();

        payment.markAsPaid(cancelPayRequestDto.getPaymentKey());
        given(paymentRepository.findByMerchantUid(cancelPayRequestDto.getPaymentKey())).willReturn(Optional.of(
            payment));
        given(matchPlayerRepository.findByUserAndMatch(payment.getUser(), payment.getMatch())).willReturn(Optional.of(
            matchPlayer));

        int refundAmount = payment.refundAmount();

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("{\"message\":\"결제 취소 실패\"}");
        when(payClient.requestCancelPay(cancelPayRequestDto.getPaymentKey(), cancelPayRequestDto.getCancelReason(),
            refundAmount)).thenReturn(mockResponse);

        /* when */
        PaymentResponse response = paymentService.requestCancelPayment(cancelPayRequestDto);

        /* then */
        assertThat(response.getErrorCode()).isEqualTo("400");
        assertThat(response.getErrorMsg()).isEqualTo("결제 취소 실패");
    }

    @Test
    @DisplayName("결제 취소 테스트")
    void testRequestCancelPayment() throws Exception {
        /* given */
        Payment payment = getPayment();
        MatchPlayer matchPlayer = getMatchPlayer(payment.getUser(), payment.getMatch());

        CancelPayRequestDto cancelPayRequestDto = CancelPayRequestDto.builder()
            .paymentKey("tviva20250410231140WtiG4")
            .cancelReason("단순 변심")
            .cancelAmount(11000)
            .build();

        payment.markAsPaid(cancelPayRequestDto.getPaymentKey());
        given(paymentRepository.findByMerchantUid(cancelPayRequestDto.getPaymentKey())).willReturn(Optional.of(
            payment));
        given(matchPlayerRepository.findByUserAndMatch(payment.getUser(), payment.getMatch())).willReturn(Optional.of(
            matchPlayer));

        int refundAmount = payment.refundAmount();

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(payClient.requestCancelPay(cancelPayRequestDto.getPaymentKey(), cancelPayRequestDto.getCancelReason(),
            refundAmount)).thenReturn(mockResponse);

        /* when */
        PaymentResponse response = paymentService.requestCancelPayment(cancelPayRequestDto);

        /* then */
        assertThat(response.getCancelReason()).isEqualTo(payment.getCancelReason());
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
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
    @DisplayName("매치 검증 후 결제")
    void testRequestPayment() {
        /* given */
        given(paymentRepository.existsPayment(user.getId(), match.getMatchId())).willReturn(false);
        RequestPaymentDto requestPaymentDto = new RequestPaymentDto(UUID.randomUUID().toString(), 11000, match, user);
        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);

        /* when */
        paymentService.requestPayment(requestPaymentDto);

        /* then */
        verify(paymentRepository, times(1)).save(captor.capture());
        Payment payment = captor.getValue();

        assertThat(payment.getImpUid()).isEqualTo(requestPaymentDto.getOrderId());
        assertThat(payment.getAmount()).isEqualTo(requestPaymentDto.getAmount());
    }

    @Test
    @DisplayName("매치 검증 후 결제 요청 Exception[결제 요청 또는 완료 이력이 있는지 검증]")
    void testRequestPaymentException() {
        /* given */
        given(paymentRepository.existsPayment(user.getId(), match.getMatchId())).willReturn(true);
        RequestPaymentDto requestPaymentDto = new RequestPaymentDto(UUID.randomUUID().toString(), 11000, match, user);

        /* then */
        assertThatThrownBy(() -> paymentService.requestPayment(requestPaymentDto))
            .isInstanceOf(ApiException.class)
            .hasMessage("이미 결제 요청한 이력이 존재합니다.");
    }

    @Test
    @DisplayName("결제 최종 승인 실패 테스트")
    void testRequestPayConfirmFail() throws Exception {
        /* given */
        SaveAmountRequest amountRequest = SaveAmountRequest.builder()
            .amount(match.getMatchPrice())
            .orderId(UUID.randomUUID().toString())
            .paymentKey("tviva20250409200902SF275")
            .build();
        
        given(matchRepository.findById(amountRequest.getMatchId())).willReturn(Optional.of(match));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String fakeAccessToken = "mocked.jwt.token";
    
        given(request.getHeader("access")).willReturn(fakeAccessToken);
        given(jwtUtil.getId(fakeAccessToken)).willReturn(user.getId());

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockResponse.body()).thenReturn("{\"message\":\"결제 실패\"}");
        when(payClient.requestPay(any())).thenReturn(mockResponse);

        HttpResponse<String> mockFailRes = mock(HttpResponse.class);
        lenient().when(mockFailRes.statusCode()).thenReturn(200); // mock 중복시 lenient 적용 (중복 stubbing 무시)
        when(payClient.requestCancelPay(anyString(), anyString(), anyInt())).thenReturn(mockFailRes);

        /* when */
        PaymentResponse response = paymentService.requestPayConfirm(request, amountRequest);

        /* then */
        assertThat(response.getOrderId()).isEqualTo(amountRequest.getOrderId());
        assertThat(response.getPaymentStatus()).isEqualTo(PaymentStatus.CANCELED);
    }

    @Test
    @DisplayName("결제 최종 승인 테스트")
    void testRequestPayConfirm() throws Exception {
        /* given */
        SaveAmountRequest amountRequest = SaveAmountRequest.builder()
            .amount(match.getMatchPrice())
            .orderId(UUID.randomUUID().toString())
            .paymentKey("tviva20250409200902SF275")
            .matchId(match.getMatchId())
            .build();

        given(matchRepository.findById(amountRequest.getMatchId())).willReturn(Optional.of(match));

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String fakeAccessToken = "mocked.jwt.token";

        given(request.getHeader("access")).willReturn(fakeAccessToken);
        given(jwtUtil.getId(fakeAccessToken)).willReturn(user.getId());

        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        when(mockResponse.statusCode()).thenReturn(200);
        when(payClient.requestPay(any())).thenReturn(mockResponse);

        ArgumentCaptor<Payment> argumentCaptor = ArgumentCaptor.forClass(Payment.class);

        /* when */
        PaymentResponse response = paymentService.requestPayConfirm(request, amountRequest);

        /* then */
        verify(paymentRepository, times(1)).save(argumentCaptor.capture());

        Payment payment = argumentCaptor.getValue();

        assertThat(response.getOrderId()).isEqualTo(payment.getImpUid());
        assertThat(response.getAmount()).isEqualTo(payment.getAmount());
        assertThat(response.getPaymentStatus()).isEqualTo(payment.getStatus());
    }

    @Test
    @DisplayName("결제 최종 승인 테스트[토스 api 실패 테스트]")
    void testTossRequestFail() throws Exception {
        /* given */
        Payment payment = getPayment();

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(400)
            .setBody(failBody)
            .addHeader("Content-Type", "application/json")
        );

        SaveAmountRequest amountRequest = SaveAmountRequest.builder()
            .amount(payment.getAmount())
            .orderId(payment.getImpUid())
            .paymentKey("tviva20250409200902SF275")
            .build();

        /* when */
        HttpResponse response = tossClient.requestPayForTest("secretKey", mockWebServerUrl, amountRequest);

        /* then */
        assertThat(response.statusCode()).isEqualTo(400);
    }

    @Test
    @DisplayName("결제 최종 승인 테스트[토스 api 성공 테스트]")
    void testTossRequest() throws Exception {
        /* given */
        Payment payment = getPayment();

        mockWebServer.enqueue(new MockResponse()
            .setResponseCode(200)
            .setBody(successBody)
            .addHeader("Content-Type", "application/json")
        );

        SaveAmountRequest amountRequest = SaveAmountRequest.builder()
            .amount(payment.getAmount())
            .orderId(payment.getImpUid())
            .paymentKey("tviva20250409200902SF275")
            .build();

        /* when */
        HttpResponse response = tossClient.requestPayForTest("secretKey", mockWebServerUrl, amountRequest);

        /* then */
        assertThat(response.statusCode()).isEqualTo(200);
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
