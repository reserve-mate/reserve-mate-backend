package com.reservemate.reserve_mate_backend.facility.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.reservemate.reserve_mate_backend.facility.domain.Facility;
import com.reservemate.reserve_mate_backend.facility.domain.FacilityManager;
import com.reservemate.reserve_mate_backend.user.domain.User;

public interface FacilityManagerRepository extends JpaRepository<FacilityManager, Long> {

    @Query(value = "select mng from FacilityManager mng where mng.user.id = :userId")
    List<FacilityManager> findByUserId(@Param("userId") Long userId);

    List<FacilityManager> findByFacility(Facility facility);

    List<FacilityManager> findByUser(User user);

}
