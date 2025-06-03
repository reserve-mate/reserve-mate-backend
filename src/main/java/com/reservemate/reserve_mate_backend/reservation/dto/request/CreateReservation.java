package com.reservemate.reserve_mate_backend.reservation.dto.request;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.user.domain.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class CreateReservation {        // 예약(대기) 생성

    private LocalDate reserveDate;      // 예약 날짜
    private LocalTime startTime;        // 시작 시간
    private LocalTime endTime;          // 종료 시간
    private Integer totalPrice;         // 총 가격
    private Long courtId;               // 선택 코트

    // 예약 시간이 현재 시간보다 과거인지 검증
    public void validateNotPast() {
        LocalDateTime todayTime = LocalDateTime.now();
        LocalDateTime reserveDateTime = LocalDateTime.of(this.reserveDate, this.startTime);

        if (reserveDateTime.isBefore(todayTime)) {
            throw new ApiException(ErrorCode.PAST_TIME_NOT_ALLOWED);
        }
    }

    public Reservation toEntity(User user, Court court) {
        String reservationNumber = court.getSportType().getPrefix() + UUID.randomUUID().toString().substring(0, 8);
        Reservation reservation = Reservation.builder()
            .court(court)
            .user(user)
            .reserveDate(this.reserveDate)
            .startTime(this.startTime)
            .endTime(this.endTime)
            .totalPrice(this.totalPrice)
            .reservationNumber(reservationNumber)
            .build();
        return reservation;
    }

}
