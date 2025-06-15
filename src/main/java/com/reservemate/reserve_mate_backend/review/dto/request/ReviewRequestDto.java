package com.reservemate.reserve_mate_backend.review.dto.request;

import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.review.domain.Review;
import com.reservemate.reserve_mate_backend.review.domain.ReviewType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ReviewRequestDto {

    private Long courtId;
    private String reservationNumber;
    private Integer rating;
    private String content;
    private ReviewType reviewType;

    // 리뷰 Entity
    public Review toEntity(Reservation reservation) {
        return Review.builder()
            .rating(this.rating)
            .reservation(reservation)
            .content(this.content)
            .reviewType(this.reviewType)
            .facility(reservation.getCourt().getFacility())
            .user(reservation.getUser())
            .build();
    }

}
