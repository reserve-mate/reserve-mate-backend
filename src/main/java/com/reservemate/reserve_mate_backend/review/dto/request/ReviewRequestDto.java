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
    private Integer rating;
    private String title;
    private String content;
    private Long reservationId;
    private ReviewType reviewType;

    public Review toEntity(Reservation reservation) {
        return Review.builder()
            .rating(this.rating)
            .title(this.title)
            .content(this.content)
            .facility(reservation.getCourt().getFacility())
            .user(reservation.getUser())
            .reservation(reservation)
            .reviewType(this.reviewType)
            .build();
    }

}
