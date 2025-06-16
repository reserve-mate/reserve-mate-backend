package com.reservemate.reserve_mate_backend.review.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewModifyRequest {

    private Long reviewId;
    private Integer rating;
    private String content;
    private List<Integer> delOrderIds;

}
