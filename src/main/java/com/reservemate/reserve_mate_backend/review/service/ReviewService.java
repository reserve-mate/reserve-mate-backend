package com.reservemate.reserve_mate_backend.review.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.review.dto.response.ReviewListResponse;
import com.reservemate.reserve_mate_backend.review.repository.ReviewCustomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewCustomRepository reviewCustomRepository;

    /* 시설 리뷰 목록 조회 */
    public Slice<ReviewListResponse> getFacilityReviews(Long facilityId, Integer pageNum) {
        Pageable pageable = PageRequest.of(pageNum, 1);
        Slice<ReviewListResponse> response = reviewCustomRepository.getReviewListResponses(facilityId, pageable);
        return response;
    }

}
