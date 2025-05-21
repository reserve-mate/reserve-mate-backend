package com.reservemate.reserve_mate_backend.reservation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;

public interface ReserveRepository extends JpaRepository<Reservation, Long> {

    /* 해당 날짜의 예약 확정된 예약 목록 조회 */
    List<Reservation> findByReserveDateAndStatus(LocalDate reserveDate, ReservationStatus confirmed);

}
