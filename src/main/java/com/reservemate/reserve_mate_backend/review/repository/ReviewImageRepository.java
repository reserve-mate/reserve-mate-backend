package com.reservemate.reserve_mate_backend.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.review.domain.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {

}
