package com.reservemate.reserve_mate_backend.review.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class ReviewCountResponse {

    private String facilityName;    // 시설 명
    private Long reviewCnt;      // 해당 시설 리뷰 개수
    private double rating;          // 해당 시설의 평점

}
