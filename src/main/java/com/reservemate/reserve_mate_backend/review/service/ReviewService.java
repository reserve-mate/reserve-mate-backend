package com.reservemate.reserve_mate_backend.review.service;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.review.repository.ReviewCustomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewCustomRepository reviewCustomRepository;

    /* 시설 리뷰 목록 조회 */
    public void getReviews(Long facilityId, Long userId) {

    }

}
