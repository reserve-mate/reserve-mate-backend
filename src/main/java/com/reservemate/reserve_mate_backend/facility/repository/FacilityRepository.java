package com.reservemate.reserve_mate_backend.facility.repository;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility,Long> {
}
