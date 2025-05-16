package com.reservemate.reserve_mate_backend.facility.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;

public interface FacilityRepository extends JpaRepository<Facility, Long>, CustomFacilityRepository {

}
