package com.reservemate.reserve_mate_backend.review.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.common.exception.ApiException;
import com.reservemate.reserve_mate_backend.common.exception.ErrorCode;
import com.reservemate.reserve_mate_backend.review.domain.Review;
import com.reservemate.reserve_mate_backend.review.domain.ReviewImage;
import com.reservemate.reserve_mate_backend.review.domain.ReviewType;
import com.reservemate.reserve_mate_backend.review.dto.response.MyReviewCntResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.MyReviewListResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewCountResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewDetailResponse;
import com.reservemate.reserve_mate_backend.review.dto.response.ReviewListResponse;
import com.reservemate.reserve_mate_backend.review.repository.ReviewCustomRepository;
import com.reservemate.reserve_mate_backend.review.repository.ReviewImageRepository;
import com.reservemate.reserve_mate_backend.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewCustomRepository reviewCustomRepository;
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

    /* 내가 작성한 리뷰 카운트 */
    public List<MyReviewCntResponse> getMyReviewCnt(Long userId) {
        return reviewCustomRepository.getMyReviewFacilityCnt(userId);
    }

    /* 내가 작성한 리뷰 */
    public Slice<MyReviewListResponse> getMyReviews(Long userId, Integer pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 6);
        return reviewCustomRepository.getMyReviewList(userId, pageable);
    }

    // 리뷰 상세
    public ReviewDetailResponse getReviewDetail(Long reviewId, ReviewType reviewType) {
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new ApiException(
            ErrorCode.NOT_FOUND_REVIEW));

        List<ReviewImage> images = reviewImageRepository.findByReview(review);

        ReviewDetailResponse response = ReviewDetailResponse.toResponse(review, images, reviewType);

        return response;
    }

    /* 리뷰 페이지 접근 시 해당 시설 리뷰 개수 가져오기 */
    public ReviewCountResponse getFacilityReviewCnt(Long facilityId) {

        ReviewCountResponse response = reviewCustomRepository.getReviewInfo(facilityId);

        return response;
    }

    /* 시설 리뷰 목록 조회 */
    public Slice<ReviewListResponse> getFacilityReviews(Long facilityId, Integer pageNum, Long userId) {
        Pageable pageable = PageRequest.of(pageNum, 6);
        Slice<ReviewListResponse> response = reviewCustomRepository.getReviewListResponses(facilityId, userId,
            pageable);
        return response;
    }

}
