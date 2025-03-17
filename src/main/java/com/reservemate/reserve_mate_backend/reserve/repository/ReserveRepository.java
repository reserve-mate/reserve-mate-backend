package com.reservemate.reserve_mate_backend.reserve.repository;

import com.reservemate.reserve_mate_backend.reserve.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserveRepository extends JpaRepository<Reservation, Long> {}
