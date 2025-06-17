package com.reservemate.reserve_mate_backend.reservation.dto.response;

import java.time.LocalDate;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReviewReservationResponse {

    private Long courtId;               // 코트 번호
    private String reservationNumber;   // 예약 번호
    private SportType sportType;        // 스포츠 타입
    private String facilityName;        // 시설 이름
    private LocalDate useDateDate;      // 이용 날짜

    /* 리뷰 작성시 예약 일부 내용 반환 값 */
    public static ReviewReservationResponse toResponse(Reservation reservation) {
        return ReviewReservationResponse.builder()
            .reservationNumber(reservation.getReservationNumber())
            .useDateDate(reservation.getReserveDate())
            .courtId(reservation.getCourtId())
            .sportType(reservation.getCourt().getSportType())
            .facilityName(reservation.getCourt().getFacility().getName())
            .build();
    }

}
