package com.reservemate.reserve_mate_backend.domain.facilities.repository;

import com.reservemate.reserve_mate_backend.domain.facilities.entity.Facilities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilitiesRepository extends JpaRepository<Facilities,Long> {
}
