package com.reservemate.reserve_mate_backend.facility.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;

public interface CourtRepository extends JpaRepository<Court, Long> {

    List<Court> findByFacilityAndActive(Facility facility, boolean b);

    List<Court> findByFacility(Facility facility);

    /* 시설들의 코트 목록 조회 */
    @Query("select c from Court c where c.facility.id in (:facilityIds)")
    List<Court> findByFacilityIds(@Param("facilityIds") List<Long> facilityIds);

}
