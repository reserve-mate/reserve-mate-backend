package com.reservemate.reserve_mate_backend.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.reservemate.reserve_mate_backend.review.domain.ReviewType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class MyReviewListResponse {

    private Long reviewId;
    private Long facilityId;
    private String facilityName;
    private Integer rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String reviewTitle;
    private String reviewContent;
    private ReviewType reviewType;
    private List<ReviewImageResponse> images;

}
