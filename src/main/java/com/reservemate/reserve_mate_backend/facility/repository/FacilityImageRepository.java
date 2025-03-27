package com.reservemate.reserve_mate_backend.facility.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityImage;

public interface FacilityImageRepository extends JpaRepository<FacilityImage, Long> {

    List<FacilityImage> findByFacility(Facility facility);

}
