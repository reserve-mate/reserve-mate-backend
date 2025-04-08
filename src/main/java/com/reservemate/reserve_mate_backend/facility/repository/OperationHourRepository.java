package com.reservemate.reserve_mate_backend.facility.repository;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reservemate.reserve_mate_backend.facility.domain.OperatingHour;

public interface OperationHourRepository extends JpaRepository<OperatingHour, Long> {

    @Query(value = "select oh from OperatingHour oh where oh.facility.id in :facilityIds and dayOfWeek = :dayOfWeek")
    List<OperatingHour> findByFacilityInAndDayOfWeek(@Param("facilityIds") List<Long> facilityIds,
        @Param("dayOfWeek") DayOfWeek dayOfWeek);

}
