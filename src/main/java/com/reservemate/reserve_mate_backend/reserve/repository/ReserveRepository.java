package com.reservemate.reserve_mate_backend.reserve.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.reserve.domain.Reservation;

public interface ReserveRepository extends JpaRepository<Reservation, Long>{

}
