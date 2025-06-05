package com.reservemate.reserve_mate_backend.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reservemate.reserve_mate_backend.match.domain.Match;
import com.reservemate.reserve_mate_backend.payment.domain.Payment;
import com.reservemate.reserve_mate_backend.payment.domain.PaymentStatus;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.user.domain.User;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByImpUid(String orderId);

    @Query("select exists (select pm from Payment pm where pm.user.id = :userId and pm.match.matchId = :matchId and (pm.status = 'READY' or pm.status = 'PAID'))")
    boolean existsPayment(@Param("userId") Long id, @Param("matchId") Long matchId);

    Optional<Payment> findByMerchantUid(String paymentKey);

    Optional<Payment> findByMatchAndUser(Match match, User user);

    @Query("select pm from Payment pm where pm.user.id = :userId and pm.match.matchId = :matchId and pm.status = 'PAID'")
    Optional<Payment> findByMatchIdAndUserId(@Param("matchId") Long matchId, @Param("userId") Long userId);

    Optional<Payment> findByMatchAndUserAndStatus(Match match, User user, PaymentStatus paid);

    /* 예약 결제 정보 조회 */
    Optional<Payment> findByReservation(Reservation reservation);

    /* 예약 결제 정보 조회 */
    @Query("select p from Payment p where p.reservation.id = :reservationId")
    Optional<Payment> findByReservationId(@Param("reservationId") Long reservationId);

    /* 해당 유저의 매치 결제 내역 카운트 */
    int countByMatchInAndUser(List<Match> matchIds, User user);

    /* 해당 유저의 예약 결제 내역 카운트 */
    int countByReservationInAndUser(List<Reservation> reservations, User user);

    /* 예약 결제 리스트 조회 */
    List<Payment> findByReservationIn(List<Reservation> reservations);

    /* 매치 결제 리스트 조회 */
    List<Payment> findByMatchIn(List<Match> matches);

}
