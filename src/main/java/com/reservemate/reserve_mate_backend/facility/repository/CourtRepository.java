package com.reservemate.reserve_mate_backend.facility.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;

public interface CourtRepository extends JpaRepository<Court, Long> {

    List<Court> findByFacilityAndActive(Facility facility, boolean b);

}
