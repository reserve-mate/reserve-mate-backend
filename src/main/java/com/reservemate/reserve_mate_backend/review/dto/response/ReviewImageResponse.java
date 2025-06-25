package com.reservemate.reserve_mate_backend.review.dto.response;

import com.reservemate.reserve_mate_backend.review.domain.ReviewImage;

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
public class ReviewImageResponse {

    private String imageUrl;
    private Integer imageOrder;

    public static ReviewImageResponse toResponse(ReviewImage reviewImage) {

        ReviewImageResponse response = ReviewImageResponse.builder()
            .imageUrl(reviewImage.getImageUrl())
            .imageOrder(reviewImage.getImageOrder())
            .build();

        return response;
    }

}
