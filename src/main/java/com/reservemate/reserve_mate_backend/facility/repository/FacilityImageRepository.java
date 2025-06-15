package com.reservemate.reserve_mate_backend.facility.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityImage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FacilityImageRepository extends JpaRepository<FacilityImage, Long> {

    List<FacilityImage> findByFacility(Facility facility);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE FacilityImage fi SET fi.deleted = true WHERE fi.facility = :facility")
    void deleteByFacility(Facility facility);

}
