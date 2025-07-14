package com.reservemate.reserve_mate_backend.reservation.domain;

import com.reservemate.reserve_mate_backend.common.entity.BaseEntity;
import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.user.domain.User;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservations")
@SQLDelete(sql = "UPDATE reservations SET deleted = true WHERE reservation_id = ?")
@SQLRestriction("deleted = false")
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id", updatable = false)
    private Long id;

    @Column(name = "reserve_date")
    private LocalDate reserveDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status;

    @Column(name = "reservation_number", nullable = false, unique = true)
    private String reservationNumber;

    @Column(name = "cancel_reason")
    private String cancelReason;

    @Column(name = "canceled_at")
    private LocalDateTime canceledAt;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id", nullable = false)
    private Court court;

    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "waiting_list_id")
    // private WaitingList waitingList;

    @Builder
    public Reservation(
        LocalTime startTime,
        LocalTime endTime,
        Integer totalPrice,
        User user,
        Court court,
        LocalDate reserveDate,
        String reservationNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = ReservationStatus.PENDING;
        this.totalPrice = totalPrice;
        this.user = user;
        this.court = court;
        this.reserveDate = reserveDate;
        this.status = ReservationStatus.PENDING;
        this.reservationNumber = reservationNumber;
        //this.waitingList = waitingList;
    }

    public Reservation(
        Long id,
        LocalTime startTime,
        LocalTime endTime,
        Integer totalPrice,
        User user,
        Court court,
        LocalDate reserveDate,
        String reservationNumber) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = ReservationStatus.PENDING;
        this.totalPrice = totalPrice;
        this.user = user;
        this.court = court;
        this.reserveDate = reserveDate;
        this.status = ReservationStatus.PENDING;
        this.reservationNumber = reservationNumber;
        //this.waitingList = waitingList;
    }

    public void confirm() {
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel(String reason) {
        this.status = ReservationStatus.CANCELED;
        this.cancelReason = reason;
        this.canceledAt = LocalDateTime.now();
    }

    public void complete() {
        if (this.status != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException("Only confirmed reservations can be completed");
        }
        this.status = ReservationStatus.COMPLETED;
    }

    // 겹치는 시간대에 예약했는지 검증
    public boolean isOverlapping(LocalTime start, LocalTime end) {
        return (start.isBefore(this.endTime) && end.isAfter(this.startTime));
    }

    // 예약 불가능한 시간대 List
    public static List<LocalTime> getUnavailableHours(List<Reservation> reservations) {

        Set<LocalTime> hours = new HashSet<>();

        for (Reservation reservation : reservations) {

            LocalTime start = reservation.getStartTime();
            LocalTime end = reservation.getEndTime();

            while (start.isBefore(end)) {
                hours.add(start);
                start = start.plusHours(1);
            }

        }

        return hours.stream().sorted().toList();
    }

    // 코트 아이디 가져오기
    public Long getCourtId() {
        return this.court.getId();
    }

    public void isNotConfirm() {
        if (this.status != ReservationStatus.CONFIRMED) {
            throw new ApiException(ErrorCode.NOT_CONFIRMED_RESERVATION);
        }
    }

    // 예약 대기 상태가 아닌 경우
    public void isNotPending() {
        if (this.status != ReservationStatus.PENDING) {
            throw new ApiException(ErrorCode.RESERVATION_NOT_PENDING);
        }
    }

    // 이미 취소된 예약인지 검증
    public void isCancel() {
        if (this.status == ReservationStatus.CANCELED) {
            throw new ApiException(ErrorCode.ALREADY_CANCELLED_RESERVATION);
        }
    }

    // 이미 취소된 예약인지 검증
    public void isNotCancel() {
        if (this.status != ReservationStatus.CANCELED) {
            throw new ApiException(ErrorCode.NOT_CANCELLED_RESERVATION);
        }
    }

    // 이미 종료된 예약약인지 검증
    public void isComplete() {
        if (this.status == ReservationStatus.COMPLETED) {
            throw new ApiException(ErrorCode.ALREADY_ENDED_RESERVATION);
        }
    }

    // 종료된 예약인지 검증 (positive)
    public void isNotComplete() {
        if (this.status != ReservationStatus.COMPLETED) {
            throw new ApiException(ErrorCode.NOT_COMPLETED_RESEVATION);
        }
    }

    // 이미 완료되거나 취소된 예약인지 검증
    public void isCompleteOrCancel() {
        List<ReservationStatus> status = List.of(ReservationStatus.CANCELED, ReservationStatus.COMPLETED);
        if (status.contains(this.status)) {
            throw new ApiException(ErrorCode.ALREADY_PROCESSED_RESERVATION);
        }
    }

    // 관리자 대시보드 총 예약 수
    public static Long getTotalReservation(List<Reservation> reservations) {
        return reservations.stream().filter((reservation) -> (reservation.getStatus() == ReservationStatus.COMPLETED)
            || (reservation.getStatus() == ReservationStatus.CONFIRMED)).count();
    }

}
