package com.reservemate.reserve_mate_backend.review.dto.response;

import java.util.List;

import com.reservemate.reserve_mate_backend.review.domain.Review;
import com.reservemate.reserve_mate_backend.review.domain.ReviewImage;

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
    private Integer rating;
    private String reviewTitle;
    private String reviewContent;
    private List<ReviewImageResponse> images;

    /* 리뷰 상세 response */
    public static ReviewDetailResponse toResponse(Review review, List<ReviewImage> images) {

        ReviewDetailResponse response = ReviewDetailResponse.builder()
            .reviewId(review.getId())
            .rating(review.getRating())
            .reviewTitle(review.getTitle())
            .reviewContent(review.getContent())
            .build();

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
