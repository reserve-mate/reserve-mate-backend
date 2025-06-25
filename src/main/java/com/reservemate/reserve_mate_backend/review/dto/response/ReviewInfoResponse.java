package com.reservemate.reserve_mate_backend.review.dto.response;

import java.time.LocalDate;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.match.domain.Match;
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
public class ReviewInfoResponse {

    private Long courtId;               // 코트 번호
    private String courtName;           // 코트 명
    private SportType sportType;        // 스포츠 타입
    private String facilityName;        // 시설 이름
    private LocalDate useDateDate;      // 이용 날짜

    /* 리뷰 작성시 매치 일부 내용 반환 값 */
    public static ReviewInfoResponse toResponse(Match match) {
        return ReviewInfoResponse.builder()
            .useDateDate(match.getMatchDate())
            .courtId(match.getCourt().getId())
            .courtName(match.getCourt().getName())
            .sportType(match.getCourt().getSportType())
            .facilityName(match.getFacility().getName())
            .build();
    }

    /* 리뷰 작성시 예약 일부 내용 반환 값 */
    public static ReviewInfoResponse toResponse(Reservation reservation) {
        return ReviewInfoResponse.builder()
            .useDateDate(reservation.getReserveDate())
            .courtId(reservation.getCourtId())
            .courtName(reservation.getCourt().getName())
            .sportType(reservation.getCourt().getSportType())
            .facilityName(reservation.getCourt().getFacility().getName())
            .build();
    }

}
