package com.reservemate.reserve_mate_backend.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PopularityReviewResponse {

    private Long facilityId;
    private double rating;

}
