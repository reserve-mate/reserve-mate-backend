package com.reservemate.reserve_mate_backend.review.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.reservemate.reserve_mate_backend.facility.domain.SportType;
import com.reservemate.reserve_mate_backend.review.domain.Review;
import com.reservemate.reserve_mate_backend.review.domain.ReviewImage;
import com.reservemate.reserve_mate_backend.review.domain.ReviewType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class ReviewDetailResponse {

    private Long reviewId;
    private String facilityName;
    private Integer rating;
    private String reviewTitle;
    private String reviewContent;
    private SportType sportType;
    private String courtName;
    private LocalDate useDate;
    private List<ReviewImageResponse> images;

    /* 리뷰 상세 response */
    public static ReviewDetailResponse toResponse(Review review, List<ReviewImage> images, ReviewType reviewType) {

        ReviewDetailResponse response = ReviewDetailResponse.builder()
            .reviewId(review.getId())
            .facilityName(review.getFacility().getName())
            .rating(review.getRating())
            .reviewTitle(review.getTitle())
            .reviewContent(review.getContent())
            .build();

        if (reviewType == ReviewType.RESERVATION) {  // 예약 리뷰인 경우
            response = response.toBuilder()
                .sportType(review.getFacility().getSportType())
                .courtName(review.getReservation().getCourt().getName())
                .useDate(review.getReservation().getReserveDate())
                .build();
        } else if (reviewType == ReviewType.MATCH) {  // 매치 리뷰인 경우
            response = response.toBuilder()
                .sportType(review.getFacility().getSportType())
                .courtName(review.getMatch().getCourt().getName())
                .useDate(review.getMatch().getMatchDate())
                .build();
        }

        if (images != null && !images.isEmpty()) {
            List<ReviewImageResponse> imageResponses = images.stream()
                .map(ReviewImageResponse::toResponse).toList();

            response = response.toBuilder()
                .images(imageResponses)
                .build();
        }

        return response;
    }

}
