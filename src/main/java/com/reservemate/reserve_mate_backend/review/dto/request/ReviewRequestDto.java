package com.reservemate.reserve_mate_backend.review.dto.request;

import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.review.domain.Review;
import com.reservemate.reserve_mate_backend.review.domain.ReviewImage;

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
    private String reservationNumber;
    private String content;

    // 리뷰 이미지 Entity
    public ReviewImage toReviewImageEntity(String imagepath, Review review, Integer order) {
        return ReviewImage.builder()
            .imageUrl(imagepath)
            .review(review)
            .imageOrder(order)
            .build();
    }

    // 리뷰 Entity
    public Review toEntity(Reservation reservation) {
        return Review.builder()
            .rating(this.rating)
            .reservationNumber(reservationNumber)
            .content(this.content)
            .facility(reservation.getCourt().getFacility())
            .user(reservation.getUser())
            .build();
    }

}
