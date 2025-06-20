package com.reservemate.reserve_mate_backend.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /* 해당 시설의 리뷰 개수 */
    Integer countByFacility(Facility facility);

}
