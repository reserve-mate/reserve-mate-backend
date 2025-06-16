package com.reservemate.reserve_mate_backend.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.reservation.domain.Reservation;
import com.reservemate.reserve_mate_backend.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    boolean existsByReservation(Reservation reservation);

}
