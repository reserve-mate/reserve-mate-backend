package com.reservemate.reserve_mate_backend.reservation.dto.response;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.domain.Slice;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.reservation.domain.ReservationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ReservationsResponse {

    private Long reservationId;                     // 예약 번호
    private ReservationStatus reservationStatus;    // 예약 상태
    private Long facilityId;                        // 예약 시설 번호
    private String facilityName;                    // 예약 시설 이름
    private String courtName;                       // 예약 코트 이름
    private SportType sportType;                    // 운동 종목
    private String address;                         // 예약 시설 주소
    private LocalDate reservationDate;              // 예약 날짜
    private LocalTime startTime;                    // 예약 시간
    private LocalTime endTime;                      // 종료 시간
    private Long reviewId;                          // 리뷰 번호

    /* 예약 목록 */

    public static Slice<ReservationsResponse> toReservationsSlice(Slice<Reservation> reservationSlice) {
        Slice<ReservationsResponse> responseSlice = reservationSlice.map(ReservationsResponse::toReservationsResponse);

        return responseSlice;
    }

    /* 예약 목록 데이터 가공 */
    private static ReservationsResponse toReservationsResponse(Reservation reservation) {
        ReservationsResponse response = ReservationsResponse.builder()
            .reservationId(reservation.getId())
            .reservationStatus(reservation.getStatus())
            .facilityId(reservation.getCourt().getFacilityId())
            .facilityName(reservation.getCourt().getFacility().getName())
            .courtName(reservation.getCourt().getName())
            .sportType(reservation.getCourt().getSportType())
            .address(reservation.getCourt().getFacility().getAddress().getFullAddress())
            .reservationDate(reservation.getReserveDate())
            .startTime(reservation.getStartTime())
            .endTime(reservation.getEndTime())
            .build();
        return response;
    }

}
