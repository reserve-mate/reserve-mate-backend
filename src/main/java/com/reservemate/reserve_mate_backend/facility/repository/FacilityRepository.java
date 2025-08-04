package com.reservemate.reserve_mate_backend.facility.repository;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FacilityRepository extends JpaRepository<Facility, Long>, CustomFacilityRepository {

    /* 관련 시설 개수 */
    @Query("select count(f) from Facility f where f.id in (:facilityIds)")
    Long countByFacilityId(@Param("facilityIds") List<Long> facilityIds);

}
