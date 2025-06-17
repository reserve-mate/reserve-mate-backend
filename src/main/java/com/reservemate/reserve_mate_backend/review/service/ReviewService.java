package com.reservemate.reserve_mate_backend.review.service;

import org.springframework.stereotype.Service;

import com.reservemate.reserve_mate_backend.review.repository.ReviewImageRepository;
import com.reservemate.reserve_mate_backend.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;

}
