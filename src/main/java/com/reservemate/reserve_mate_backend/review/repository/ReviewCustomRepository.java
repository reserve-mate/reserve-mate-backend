package com.reservemate.reserve_mate_backend.review.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.reservemate.reserve_mate_backend.review.dto.response.ReviewCountResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewListResponse;

public interface ReviewCustomRepository {

    /* 리뷰 목록 조회 */
    Slice<ReviewListResponse> getReviewListResponses(Long facilityId, Long userId, Pageable pageable);

    /* 해당 시설의 리뷰 정보 조회 */
    ReviewCountResponse getReviewInfo(Long facilityId);

}
