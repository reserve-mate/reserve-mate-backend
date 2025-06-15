package com.reservemate.reserve_mate_backend.facility.repository;

import com.reservemate.reserve_mate_backend.facility.domain.Court;
import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourtRepository extends JpaRepository<Court, Long> {

    List<Court> findByFacilityAndActive(Facility facility, boolean b);

    List<Court> findByFacility(Facility facility);

    /* 시설들의 코트 목록 조회 */
    @Query("select c from Court c where c.facility.id in (:facilityIds)")
    List<Court> findByFacilityIds(@Param("facilityIds") List<Long> facilityIds);

    Optional<Court> findByIdAndFacility(Long courtId, Facility facility);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Court c SET c.deleted = true WHERE c.facility = :facility")
    void softDeleteByFacility(Facility facility);

}
