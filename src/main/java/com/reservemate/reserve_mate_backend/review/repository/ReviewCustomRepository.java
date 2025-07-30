package com.reservemate.reserve_mate_backend.review.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.reservemate.reserve_mate_backend.review.dto.response.MyReviewCntResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.MyReviewListResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.PopularityReviewResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewCountResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewListResponse;

public interface ReviewCustomRepository {

    List<PopularityReviewResponse> getHighRate();

    /* 내가 쓴 리뷰 목록 */
    Slice<MyReviewListResponse> getMyReviewList(Long userId, Pageable pageable);

    /* 내가 쓴 리뷰 카운트 */
    List<MyReviewCntResponse> getMyReviewFacilityCnt(Long userId);

    /* 리뷰 목록 조회 */
    Slice<ReviewListResponse> getReviewListResponses(Long facilityId, Long userId, Pageable pageable);

    /* 해당 시설의 리뷰 정보 조회 */
    ReviewCountResponse getReviewInfo(Long facilityId);

}
