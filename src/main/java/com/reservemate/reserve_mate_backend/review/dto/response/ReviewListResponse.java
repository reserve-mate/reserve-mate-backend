package com.reservemate.reserve_mate_backend.review.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
public class ReviewListResponse {

    private Long reviewId;
    private Long userId;
    private String userName;
    private Integer rating;
    private LocalDateTime reviewDate;
    private String reviewTitle;
    private String reviewContent;
    private boolean isWrite;
    private List<ReviewImageResponse> reviewImages;

}
