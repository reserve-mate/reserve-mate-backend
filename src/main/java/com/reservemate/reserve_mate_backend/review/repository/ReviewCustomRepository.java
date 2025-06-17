package com.reservemate.reserve_mate_backend.review.repository;

import java.util.List;

import com.reservemate.reserve_mate_backend.review.dto.response.ReviewListResponse;

public interface ReviewCustomRepository {

    /* 리뷰 목록 조회 */
    List<ReviewListResponse> getReviewListResponses(Long facilityId, Long userId);

}
