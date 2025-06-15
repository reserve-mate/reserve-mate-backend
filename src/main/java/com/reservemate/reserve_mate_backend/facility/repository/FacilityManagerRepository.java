package com.reservemate.reserve_mate_backend.facility.repository;

import java.util.List;
import java.util.Optional;

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

    @Query(value = "select mng from FacilityManager mng where mng.user.id = :userId and mng.facility.id = :facilityId")
    Optional<FacilityManager> findByUserIdAndfacilityId(@Param("userId") Long userId,
        @Param("facilityId") Long facilityId);

    boolean existsByUser_IdAndFacility_Id(Long userId, Long facilityId);

}
