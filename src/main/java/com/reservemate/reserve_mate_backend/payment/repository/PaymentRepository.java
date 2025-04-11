package com.reservemate.reserve_mate_backend.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reservemate.reserve_mate_backend.payment.domain.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByImpUid(String orderId);

    @Query("select exists (select pm from Payment pm where pm.user.id = :userId and pm.match.matchId = :matchId and (pm.status = 'READY' or pm.status = 'PAID'))")
    boolean existsPayment(@Param("userId") Long id, @Param("matchId") Long matchId);

    Optional<Payment> findByMerchantUid(String paymentKey);

}
