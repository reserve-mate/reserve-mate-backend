package com.reservemate.reserve_mate_backend.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.reservemate.reserve_mate_backend.match.domain.MatchStatus;
import com.reservemate.reserve_mate_backend.match.repository.MatchRepository;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.user.domain.User;
import com.reservemate.reserve_mate_backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Transactional
public class PaymentRepositoryTest {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private CourtRepository courtRepository;

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacilityManagerRepository facilityManagerRepository;

    @Autowired
    private PaymentRepository paymentRepository;

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
    @DisplayName("결제 중복 데이터 조회")
    void testExistsPayment() {
        /* given */
        Payment payment = getPayment();
        paymentRepository.save(payment);

        /* when */
        boolean existPayment = paymentRepository.existsPayment(user.getId(), match.getMatchId());

        /* then */
        assertThat(existPayment).isEqualTo(true);

    }

    private Payment getPaymentLoop(User user) {
        Payment payment = Payment.builder()
            .impUid(UUID.randomUUID().toString())
            .amount(11000)
            .payMethod("카드")
            .user(user)
            .match(match)
            .build();
        return payment;
    }

    private Payment getPayment() {
        Payment payment = Payment.builder()
            .impUid(UUID.randomUUID().toString())
            .amount(11000)
            .payMethod("카드")
            .user(user)
            .match(match)
            .build();
        return payment;
    }

    private Match getMatch(Court court, FacilityManager manager) {
        Match match = Match.builder()
            .matchName("매치")
            .matchStatus(MatchStatus.APPLICABLE)
            .teamCapacity(18)
            .matchDate(LocalDate.now())
            .matchTime(18)
            .endTime(20)
            .matchPrice(11000)
            .court(court)
            .facilityManager(manager)
            .build();

        Match saveMatch = matchRepository.save(match);
        return saveMatch;
    }

    // 매니저 데이터 저장
    private FacilityManager getFacilityManager(User user, Facility facility) {
        FacilityManager manager = FacilityManager.builder()
            .facility(facility)
            .user(user)
            .build();

        FacilityManager saveManager = facilityManagerRepository.save(manager);
        return saveManager;
    }

    private Facility getFacility() {
        Address address = Address.builder()
            .city("서울")
            .build();

        Facility facility = Facility.builder()
            .name("시설")
            .sportType(SportType.FUTSAL)
            .address(address)
            .conventient("1010")
            .build();

        Facility saveFacility = facilityRepository.save(facility);
        return saveFacility;
    }

    private Court getCourt(Facility facility) {

        Court court = Court.builder()
            .name("운동 코트")
            .courtType(CourtType.ARTIFICIAL_TURF_FUTSAL)
            .width(20)
            .height(40)
            .indoor(false)
            .fee(0)
            .facility(facility)
            .build();
        Court saveUser = courtRepository.save(court);
        return saveUser;
    }

    /* 회원 기본 설정 */
    private User getUserLoop(int num) {
        User user = User.builder()
            .name("이름")
            .email("email" + num + "@email.com")
            .password("password")
            .phone("0100000000" + num)
            .build();
        User savUser = userRepository.save(user);
        return savUser;
    }

    /* 회원 기본 설정 */
    private User getUser() {
        User user = User.builder()
            .name("이름")
            .email("email@email.com")
            .password("password")
            .phone("01000000000")
            .build();
        User savUser = userRepository.save(user);
        return savUser;
    }
}
