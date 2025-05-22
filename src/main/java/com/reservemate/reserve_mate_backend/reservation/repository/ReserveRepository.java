package com.reservemate.reserve_mate_backend.reservation.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;
import com.reservemate.reserve_mate_backend.user.domain.User;

public interface ReserveRepository extends JpaRepository<Reservation, Long> {

    /* 해당 날짜의 예약 확정된 예약 목록 조회 */
    List<Reservation> findByReserveDateAndStatus(LocalDate reserveDate, ReservationStatus confirmed);

    /* 해당 시간대에 에약이 존재하는 검증 */
    @Query("select case when count(r) > 0 then true else false end"
        + " from Reservation r"
        + " where r.reserveDate = :reserveDate"
        + " and r.startTime < :endTime"
        + " and r.endTime > :startTime"
        + " and r.status = :status"
        + " and r.court.id = :courtId")
    boolean existsReservationDate(@Param("reserveDate") LocalDate reserveDate, @Param("startTime") LocalTime startTime,
        @Param("endTime") LocalTime endTime, @Param("courtId") Long courtId,
        @Param("status") ReservationStatus confirmed);

    /* 중복된 대기 또는 확정 상태인 예약이 있는지 검증 */
    boolean existsByReserveDateAndStartTimeAndEndTimeAndStatusInAndUserAndCourt(LocalDate reserveDate,
        LocalTime startTime, LocalTime endTime, List<ReservationStatus> status, User user, Court court);

}
